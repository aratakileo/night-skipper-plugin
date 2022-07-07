package pextystudios.nightskipper;

import pextystudios.nightskipper.util.LoggerUtil;
import pextystudios.nightskipper.util.PlayerUtil;

import java.util.HashMap;

public class ConditionEngine {
    public interface ConditionGetterInterface {
        public int get();
    }

    private final HashMap<String, ConditionGetterInterface> getters = new HashMap<>();
    private final HashMap<String, Integer> vars = new HashMap<>();

    public void addGetter(String varName, ConditionGetterInterface getter) {
        if (getters.containsKey(varName) || vars.containsKey(varName)) err("variable `" + varName + "` is already defined!");

        getters.put(varName, getter);
    }

    public void addVar(String varName, int value) {
        if (getters.containsKey(varName) || vars.containsKey(varName)) err("variable `" + varName + "` is already defined!");

        vars.put(varName, value);
    }

    public boolean exec(String lvalue, String rvalue, String op) {
        int[] values = new int[2];
        int[] value = getFinalValue(lvalue);

        if (value == null) return false;
        values[0] = value[0];

        value = getFinalValue(rvalue);
        if (value == null) return false;
        values[1] = value[0];


        LoggerUtil.warn(values[0] + op + values[1]);

        switch (op) {
            case "==":
                return values[0] == values[1];
            case "!=":
                return values[0] != values[1];
            case ">=":
                return values[0] >= values[1];
            case "<=":
                return values[0] <= values[1];
            case ">":
                return values[0] > values[1];
            case "<":
                return values[0] < values[1];
        }

        err("invalid operator: `" + op + '`');
        return false;
    }

    private String getValueType(String value) {
        if (value.matches("^\\d+$")) return "num";
        if (value.matches("^\\w+$")) return "var";
        if (value.matches("^\\d+%$")) return "percent";

        return null;
    }

    private int[] getFinalValue(String value) {
        String type = getValueType(value);

        if (type == null) {
            err("invalid condition value: `" + value + '`');
            return null;
        }

        switch (type) {
            case "num":
                return new int[] {Integer.getInteger(value)};
            case "percent":
                return new int[] {(int) ((double)PlayerUtil.getPlayerCount() * (Double.parseDouble(value.substring(0, value.length() - 1)) / 100.0))};
            case "var":
                if (getters.containsKey(value))
                    return new int[]{getters.get(value).get()};

                if (vars.containsKey(value))
                    return new int[] {vars.get(value)};

                err("value variable `" + value + "` is not defined");
        }

        return null;
    }

    private void err(String e) {
        LoggerUtil.err("ConditionEngine: " + e);
    }
}
