package com.upc.finance.service.impl;

import com.upc.finance.model.dto.CashFlowResponseDto;
import com.upc.finance.model.entity.CashFlow;
import com.upc.finance.model.entity.PeriodDetail;
import com.upc.finance.model.entity.CorporateBond;
import com.upc.finance.model.enums.GracePeriodType;
import com.upc.finance.model.enums.InterestRateType;
import com.upc.finance.model.enums.Role;
import com.upc.finance.repository.PeriodDetailRepository;
import com.upc.finance.repository.CashFlowRepository;
import com.upc.finance.service.CashFlowService;
import com.upc.finance.service.strategy.InvestorCashFlowCalculator;
import com.upc.finance.service.strategy.IssuerCashFlowCalculator;
import com.upc.finance.shared.exception.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CashFlowServiceImpl implements CashFlowService {

    CashFlowRepository cashFlowRepository;
    PeriodDetailRepository periodDetailRepository;
    ModelMapper modelMapper;
    InvestorCashFlowCalculator investorCalculator;
    IssuerCashFlowCalculator issuerCalculator;

    public CashFlowServiceImpl(CashFlowRepository cashFlowRepository, ModelMapper modelMapper, PeriodDetailRepository periodDetailRepository) {
        this.cashFlowRepository = cashFlowRepository;
        this.modelMapper = modelMapper;
        this.periodDetailRepository = periodDetailRepository;
    }


    @Override
    public CashFlow generateCashFlow(Long userId, CorporateBond bond) {
        Role userRole = bond.getUser().getRole();
        investorCalculator = new InvestorCashFlowCalculator();
        issuerCalculator = new IssuerCashFlowCalculator();

        // copy bond to newBond to avoid modifying the original bond
        CorporateBond newBond = cloneBondWithoutSideEffects(bond);

        // set % to decimal
        newBond.setInterestRate(newBond.getInterestRate() / 100);
        newBond.setCok(newBond.getCok() / 100);
        newBond.setRedemptionPremium(newBond.getRedemptionPremium() / 100);
        newBond.setStructuringCost(newBond.getStructuringCost() / 100);
        newBond.setPlacementCost(newBond.getPlacementCost() / 100);
        newBond.setFlotationCost(newBond.getFlotationCost() / 100);
        newBond.setCavaliCost(newBond.getCavaliCost() / 100);

        // 1 si es nominal pasar a efectivo
        if (newBond.getInterestRateType() == InterestRateType.NOMINAL) {
            // TN -> TE => TNA c.m. -> TES
            // TE = (1 + TN / m) ^ n - 1
            // m=dias TN/ dias capitalizacion => 360/30 =12
            // n=dias TE/ dias capitalizacion => 180/30 =6
            // plazo de pago = semestral = 6 meses = 180 dias
            var m = newBond.getInterestRateFrequency()/ newBond.getInterestRateCapitalizationFrequency();
            var n = newBond.getPaymentFrequency()/ newBond.getInterestRateCapitalizationFrequency();
            var result = Math.pow((1 + newBond.getInterestRate() / m), n) - 1;
            newBond.setInterestRate(result);
            System.out.println("Interest rate converted from nominal to effective: " + result);
        }

        // 1 si es efectivo pasar al correcto efectivo
        if(newBond.getInterestRateType() == InterestRateType.EFFECTIVE) {
            // TE -> TE => TEA -> TES
            // TE2 = (1 + TE1) ^ (n2/n1) - 1 => TE2 = (1 + TE1) ^ (360/180) - 1
            // n2=dias TE2 => 360
            // n1=dias TE1 => 180
            var n2 = newBond.getPaymentFrequency();
            var n1 = newBond.getInterestRateFrequency();
            var result = Math.pow((1 + newBond.getInterestRate()), ((double) n2 / n1)) - 1;
            newBond.setInterestRate(result);
            System.out.println("Interest rate converted from effective to effective: " + result);
        }

        // 2 modificar tasa de cok
        //cok TEA -> TES
        if(!Objects.equals(newBond.getCokFrequency(), newBond.getPaymentFrequency())) {
            // TEA -> TES
            // COK2 = (1 + COK1) ^ (n2/n1) - 1 => COK2 = (1 + COK1) ^ (360/180) - 1
            // n2=dias COK2 => 360
            // n1=dias COK1 => 180
            var n2 = newBond.getPaymentFrequency();
            var n1 = newBond.getCokFrequency();
            var result = Math.pow((1 + newBond.getCok()), ((double) n2 / n1)) - 1;
            newBond.setCok(result);
            System.out.println("COK converted: " + result);
        }

        // 3 calculate initial interest
        var interest = newBond.getNominalValue() * newBond.getInterestRate();
        System.out.println("Interest calculated: " + interest);

        // 4 PLAN DE PAGOS
        // plazo de pago = 3 años * 360 , frecuencia de pago = 180 dias , periodos = 3 * 360/ 180 = 6
        // 6 periodos de pago
        var paymentPeriods = (newBond.getPaymentPeriod()*360) / (newBond.getPaymentFrequency());
        System.out.println("Payment periods calculated: " + paymentPeriods);

        List<Double> amortizationList = new ArrayList<>(List.of());
        List<Double> insterestList = new ArrayList<>(List.of());
        List<Double> paymentList = new ArrayList<>(List.of());
        List<Double> nominalValueList = new ArrayList<>(List.of());
        List<Double> balanceList = new ArrayList<>(List.of());
        List<Double> flowList = new ArrayList<>(List.of());

        // Añadir inversión -> compra del bono P0
        nominalValueList.add(0.0);
        amortizationList.add(0.0);
        insterestList.add(0.0);
        paymentList.add(0.0);
        balanceList.add(0.0);

        // Determinar el flujo inicial según el rol
        if (userRole.equals(Role.INVESTOR)) {
            // Para el inversor: paga por el bono más los costos de flotación y CAVALI
            flowList.add(-newBond.getNominalValue() * (1 + newBond.getFlotationCost() + newBond.getCavaliCost()));
        } else if (userRole.equals(Role.ISSUER)) {
            // Para el emisor: recibe el dinero del bono menos todos los costos
            flowList.add(newBond.getNominalValue() * (1 - newBond.getStructuringCost() - newBond.getPlacementCost() -
                    newBond.getFlotationCost() - newBond.getCavaliCost()));
        }

        // iterate over payment flowList P1 -> P5
        for (int i = 1; i < paymentPeriods; i++) {
            nominalValueList.add(newBond.getNominalValue());
            amortizationList.add(0.0);
            insterestList.add(newBond.getNominalValue()*newBond.getInterestRate()); // interes
            paymentList.add(newBond.getNominalValue()*newBond.getInterestRate()); // cuota de pago
            balanceList.add(newBond.getNominalValue()); // saldo pendiente
            flowList.add(paymentList.get(i)); // flujo de caja
        }
        // añadir ultimo periodo -> intereses + prima redención + valor nominal -> P6
        nominalValueList.add(newBond.getNominalValue()); // valor nominal en el ultimo periodo
        amortizationList.add(nominalValueList.get(paymentPeriods-1)); // amortization in the last period
        insterestList.add(newBond.getNominalValue()*(newBond.getInterestRate()+newBond.getRedemptionPremium()));
        paymentList.add(newBond.getNominalValue()*(newBond.getInterestRate()+newBond.getRedemptionPremium())+newBond.getNominalValue()); // añadir pago de interes + valor nominal
        balanceList.add(0.0); // saldo pendiente en el ultimo periodo
        flowList.add(paymentList.get(paymentPeriods)); // flujo de caja en el ultimo periodo

        // 5 PRECIO DEL BONO -> flujo de caja / (1 + COK) ^ i
        double bondPrice = 0.0;
        for (int i = 1; i < flowList.size(); i++) {
            var period = flowList.get(i);

            var discountFactor = Math.pow((1 + newBond.getCok()), i);
            bondPrice += period / discountFactor;
        }

        // 6 guardar flujo de caja en cash flow
        CashFlow cashFlow = CashFlow.builder()
                .corporateBond(bond)
                .periodsNumber(paymentPeriods)
                .maxPrice(bondPrice)
                .convexity(0.0) // Convexity calculation isn't implemented yet
                .duration(0.0) // Duration calculation not implemented yet
                .modifiedDuration(0.0) // Modified duration calculation not implemented yet
                .tcea(0.0) // TCEA not calculated for investor role
                .trea(0.0)// TREA not calculated for issuer role
                .build();
        cashFlowRepository.save(cashFlow);

        // 7 crear periodos de pago
        List<PeriodDetail> newPeriodDetails = new ArrayList<>();
        // si hay periodo de gracia, graceType = TOTAL
        // cantidad de periodos de gracia = 2
        //
        // capitalización de intereses -> modificar el valor nominal -> modificar el gracePeriodType
        //

        // Determinar los periodos que son periodos de gracia
        int gracePeriods = newBond.getGracePeriods();
        GracePeriodType graceType = newBond.getGracePeriodType();
        System.out.println("gracePeriods: " + gracePeriods);

        System.out.println("Periods size: " + (flowList.size()-1));
        for (int i = 0; i < flowList.size(); i++) {

            double currentNominalValue = nominalValueList.get(i);
            double currentInterest = insterestList.get(i);
            double currentAmortization = amortizationList.get(i);
            double currentPayment = paymentList.get(i);
            double currentBalance = balanceList.get(i);
            double currentFlow = flowList.get(i);

            // Determinar el tipo de período de gracia para este período específico
            GracePeriodType periodGraceType = GracePeriodType.NONE; // Por defecto, no es período de gracia

            // Asignar el tipo de gracia solo a los períodos que son de gracia
            if (i > 0 && i <= gracePeriods) {
                periodGraceType = graceType; // Usar el tipo de gracia definido en el bono
            }

            // Aplicar modificaciones por periodo de gracia (solo para períodos > 0 y <= gracePeriods)
            if (i > 0 && i <= gracePeriods) {
                switch (graceType) {
                    case TOTAL:

                        System.out.println("Entered to TOTAL grace period logic");
                        // En gracia total: no se paga interés ni amortización
                        // Los intereses se capitalizan (se añaden al nominal del siguiente periodo)
                        if (i < flowList.size() - 1) {
                            currentInterest = currentNominalValue * newBond.getInterestRate();

                            // Actualizar el valor nominal para el siguiente periodo
                            double interestToCapitalize = currentInterest;
                            nominalValueList.set(i + 1, nominalValueList.get(i + 1) + interestToCapitalize);
                            System.out.println(" Interes capitalizado para el periodo " + (i + 1) + ": " + interestToCapitalize);
                            System.out.println("Nominal value: " + nominalValueList.get(i + 1));

                            // Propagar este cambio a todos los periodos posteriores
                            for (int j = i + 2; j < flowList.size(); j++) {
                                nominalValueList.set(j, nominalValueList.get(j) + interestToCapitalize);
                            }

                            // Recalcular los intereses para todos los periodos posteriores con el nuevo valor nominal
                            for (int j = i + 1; j < flowList.size() - 1; j++) {
                                insterestList.set(j, nominalValueList.get(j) * newBond.getInterestRate());
                                // Actualizar también el pago y el flujo
                                paymentList.set(j, insterestList.get(j));
                                flowList.set(j, userRole.equals(Role.INVESTOR) ? paymentList.get(j) : -paymentList.get(j));
                            }

                            // Actualizar el último periodo (incluye amortización del valor nominal)
                            int lastIndex = flowList.size() - 1;
                            insterestList.set(lastIndex, nominalValueList.get(lastIndex) *
                                    (newBond.getInterestRate() + newBond.getRedemptionPremium()));
                            paymentList.set(lastIndex, insterestList.get(lastIndex) + nominalValueList.get(lastIndex));
                            flowList.set(lastIndex, userRole.equals(Role.INVESTOR) ?
                                    paymentList.get(lastIndex) : -paymentList.get(lastIndex));

                            currentAmortization = 0.0;
                            currentPayment = 0.0;
                            currentFlow = 0.0;
                            currentBalance = currentNominalValue;
                        }
                        break;

                    case PARTIAL:
                        // En gracia parcial: se pagan intereses pero no amortización
                        currentAmortization = 0.0;
                        currentPayment = currentInterest;
                        // El flujo mantiene el signo según el rol
                        currentFlow = userRole.equals(Role.INVESTOR) ? currentInterest : -currentInterest;
                        currentBalance = currentNominalValue;
                        break;

                    case NONE:
                    default:
                        System.out.println("Entered to NONE grace period logic");
                        System.out.println("Interes para el periodo " + i + ": " + currentInterest);
                        System.out.println("Nominal value: " + nominalValueList.get(i + 1));

                        // Sin periodo de gracia: solo ajustamos el signo del flujo según el rol
                        if (userRole.equals(Role.ISSUER) && i > 0) {
                            currentFlow = -currentFlow; // Para ISSUER, los flujos son negativos
                        }
                        break;
                }
            } else if (i > 0) {
                System.out.println("Entrando a periodo normal");
                System.out.println("Interes para el periodo " + i + ": " + currentInterest);
                System.out.println("Nominal value: " + nominalValueList.get(i));
                // Periodos normales después del periodo de gracia
                if (userRole.equals(Role.ISSUER)) {
                    currentFlow = -currentFlow; // Para ISSUER, los flujos son negativos
                }
            }

            // Actualizar las listas con los valores modificados
            insterestList.set(i, currentInterest);
            amortizationList.set(i, currentAmortization);
            paymentList.set(i, currentPayment);
            balanceList.set(i, currentBalance);
            flowList.set(i, currentFlow);

            PeriodDetail periodDetail = PeriodDetail.builder()
                    .period(i)
                    .nominalValue(currentNominalValue)
                    .interest(currentInterest)
                    .amortization(currentAmortization)
                    .payment(currentPayment)
                    .balance(currentBalance)
                    .flow(currentFlow)
                    .graceType(periodGraceType)
                    .cashFlow(cashFlow)
                    .build();
            periodDetail.setCashFlow(cashFlow);
            periodDetailRepository.save(periodDetail);

            newPeriodDetails.add(periodDetail);
            System.out.println("*******************************************");
        }


        // 8 CALCULAR DURACION, DURACION MODIFICADA, CONVEXIDAD
        // duracion -> es tiempo -> años
        // duracion = sum( (flujo de caja / (1 + cok) ^ i ) * i) / precio del bono
        var sumForDuration = 0.0;
        for (Double period : flowList) {
            sumForDuration += period / Math.pow(1 + newBond.getCok(), flowList.indexOf(period)) * flowList.indexOf(period);
        }
        var duration = sumForDuration / bondPrice;
        cashFlow.setDuration(validateNumeric(duration));
        //
        // duracion modificada -> expresada en % -> por cada 1% de variacion de la tasa de interes, varía el valor del bono en __%
        // duracion/(1 + cok)
        var modifiedDuration = cashFlow.getDuration() / (1 + newBond.getCok());
        cashFlow.setModifiedDuration(validateNumeric(modifiedDuration));
        //
        // convexidad -> (1/ (precio del bono * (1 + cok) ^ 2)) * (sum( flujo de caja / (1 + cok) ^ i * i (i + 1) ) )
        var sumForConvexity = 0.0;
        for (int i = 0; i < flowList.size(); i++) {
            sumForConvexity += (flowList.get(i) / Math.pow(1 + newBond.getCok(), i)) * i * (i + 1);
        }
        var convexity = (1 / (bondPrice * Math.pow(1 + newBond.getCok(), 2))) * sumForConvexity;
        cashFlow.setConvexity(validateNumeric(convexity));
        //
        // save cash flow with calculated values
         var updatedCashFlow = cashFlowRepository.save(cashFlow);

        cashFlow.setTrea(validateNumeric(investorCalculator.calculate(cashFlow, flowList, newBond.getCok(), newBond.getPaymentFrequency())));
        cashFlow.setTcea(validateNumeric(issuerCalculator.calculate(cashFlow, flowList, newBond.getCok(), newBond.getPaymentFrequency())));
        cashFlowRepository.save(cashFlow);
        return cashFlow;
    }

    private double validateNumeric(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return 0.0;
        }
        return value;
    }

    @Override
    public CashFlowResponseDto getCashFlowByCorporateBondId(Long corporateBondId) {
        var cashFlow = cashFlowRepository.findByCorporateBond_Id(corporateBondId)
                .orElseThrow(() -> new ResourceNotFoundException("Cash flow not found for corporate bond with id: " + corporateBondId));
        return modelMapper.map(cashFlow, CashFlowResponseDto.class);
    }

    @Override
    public List<PeriodDetail> getAllPeriodDetailsByCashFlowId(Long cashFlowId) {
        var periodDetails = periodDetailRepository.findAllByCashFlow_Id(cashFlowId);
        if (periodDetails.isEmpty()) {
            throw new ResourceNotFoundException("No period details found for cash flow with id: " + cashFlowId);
        }
        return periodDetails;
    }

    public CorporateBond cloneBondWithoutSideEffects(CorporateBond original) {
        CorporateBond clone = new CorporateBond();

        // copy all fields from original to clone
        clone.setNominalValue(original.getNominalValue());
        clone.setCommercialValue(original.getCommercialValue());
        clone.setInterestRate(original.getInterestRate());
        clone.setInterestRateType(original.getInterestRateType());
        clone.setInterestRateFrequency(original.getInterestRateFrequency());
        clone.setInterestRateCapitalizationFrequency(original.getInterestRateCapitalizationFrequency());
        clone.setCurrency(original.getCurrency());
        clone.setPaymentPeriod(original.getPaymentPeriod());
        clone.setPaymentFrequency(original.getPaymentFrequency());
        clone.setRedemptionPremium(original.getRedemptionPremium());
        clone.setCok(original.getCok());
        clone.setCokFrequency(original.getCokFrequency());
        clone.setGracePeriodType(original.getGracePeriodType());
        clone.setGracePeriods(original.getGracePeriods());
        clone.setStructuringCost(original.getStructuringCost());
        clone.setPlacementCost(original.getPlacementCost());
        clone.setFlotationCost(original.getFlotationCost());
        clone.setCavaliCost(original.getCavaliCost());

        clone.setUser(original.getUser());

        clone.setId(null);

        return clone;
    }


}
