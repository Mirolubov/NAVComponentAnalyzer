package com.company.model.search;

public class PrintResult {
    public static boolean print(String caption, Object[][] result) {
        if(result.length == 0) {
            return false;
        }
        System.out.println(caption + " occurs " + result.length + " times.");
        for (int i = 0; i < result.length; i++) {
            if (result[i].length != 5) {
                throw new IllegalArgumentException("Wrong result of search");
            }
            System.out.printf("%s (%s) %s\r\n\tline:%s\r\n\t%s",
                    (String) result[i][0]
                    , (String) result[i][2]
                    , (String) result[i][1]
                    , (String) result[i][3]
                    , (String) result[i][4]);

            System.out.print("\r\n");
        }
        return true;
    }
}
