package core.common;

public class CurrencyInfo {
    final Double USD = 1300.0;
    final Double JPY = 900.0;
    final Double CNY = 180.0;

    public Double exchange(String curC, Double amt) {
        if("USD".equals(curC)) {
            return amt / USD;
        } else if("JPY".equals(curC)) {
            return amt / JPY;
        }else if("CNY".equals(curC)) {
            return amt / CNY;
        }
        return -1.0;
    }
}
