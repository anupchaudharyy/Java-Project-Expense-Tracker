package com.expensetracker.service;

import com.expensetracker.model.Expense;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.List;

public class ReportService {

    @SuppressWarnings("unchecked")
    public String generateJsonReport(List<Expense> expenses) {
        JSONArray jsonArray = new JSONArray();
        for (Expense expense : expenses) {
            JSONObject expenseJson = new JSONObject();
            expenseJson.put("id", expense.getId());
            expenseJson.put("date", expense.getExpenseDate().toString());
            expenseJson.put("category", expense.getCategoryName());
            expenseJson.put("amount", expense.getAmount().doubleValue());
            expenseJson.put("description", expense.getDescription());
            jsonArray.add(expenseJson);
        }

        JSONObject report = new JSONObject();
        report.put("expenses", jsonArray);

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
