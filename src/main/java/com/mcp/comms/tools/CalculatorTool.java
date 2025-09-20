package com.mcp.comms.tools;

import org.springframework.stereotype.Component;

@Component
public class CalculatorTool {

    public double calculate(double a, double b, String operator) {
        return switch (operator) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> (b != 0) ? a / b : Double.NaN;
            default -> Double.NaN;
        };
    }
}
