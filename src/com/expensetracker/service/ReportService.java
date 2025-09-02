package com.expensetracker.service;

import com.expensetracker.model.Expense;
import com.expensetracker.model.Income;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

public class ReportService {

    @SuppressWarnings("unchecked")
    public String generateJsonReport(List<Expense> expenses, List<Income> incomes) {
        JSONArray expenseArray = new JSONArray();
        for (Expense expense : expenses) {
            JSONObject expenseJson = new JSONObject();
            expenseJson.put("id", expense.getId());
            expenseJson.put("date", expense.getExpenseDate().toString());
            expenseJson.put("category", expense.getCategoryName());
            expenseJson.put("amount", expense.getAmount().doubleValue());
            expenseJson.put("description", expense.getDescription());
            expenseArray.add(expenseJson);
        }

        JSONArray incomeArray = new JSONArray();
        for (Income income : incomes) {
            JSONObject incomeJson = new JSONObject();
            incomeJson.put("id", income.getId());
            incomeJson.put("date", income.getIncomeDate().toString());
            incomeJson.put("category", income.getCategoryName());
            incomeJson.put("amount", income.getAmount().doubleValue());
            incomeJson.put("description", income.getDescription());
            incomeArray.add(incomeJson);
        }

        JSONObject report = new JSONObject();
        report.put("incomes", incomeArray);
        report.put("expenses", expenseArray);

        return prettyPrintJson(report.toJSONString());
    }

    private String prettyPrintJson(String jsonString) {
        StringBuilder prettyJson = new StringBuilder();
        int indentLevel = 0;
        boolean inQuote = false;
        for (char c : jsonString.toCharArray()) {
            switch (c) {
                case '{':
                case '[':
                    prettyJson.append(c).append("\n");
                    indentLevel++;
                    appendIndent(prettyJson, indentLevel);
                    break;
                case '}':
                case ']':
                    prettyJson.append("\n");
                    indentLevel--;
                    appendIndent(prettyJson, indentLevel);
                    prettyJson.append(c);
                    break;
                case ',':
                    prettyJson.append(c).append("\n");
                    appendIndent(prettyJson, indentLevel);
                    break;
                case '"':
                    prettyJson.append(c);
                    inQuote = !inQuote;
                    break;
                case ':':
                    prettyJson.append(c).append(" ");
                    break;
                default:
                    if (inQuote) {
                        prettyJson.append(c);
                    } else if (!Character.isWhitespace(c)) {
                        prettyJson.append(c);
                    }
            }
        }
        return prettyJson.toString();
    }

    private void appendIndent(StringBuilder sb, int indentLevel) {
        for (int i = 0; i < indentLevel; i++) {
            sb.append("  ");
        }
    }
}

