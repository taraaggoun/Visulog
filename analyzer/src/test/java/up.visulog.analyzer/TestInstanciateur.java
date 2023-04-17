package up.visulog.analyzer;

import up.visulog.analyzer.ActivityUserGraphPlugin;
import up.visulog.analyzer.AnalyzerResult;

/**
 * Cette classe test l'instanciateur du fichier Analyzer.java
 */
class TestInstanciateur {

    public TestInstanciateur() {
        System.out.println("OK");
    }

    public TestInstanciateur(String s) {
        System.out.println("Type reconnu !");
    }

    public String toString() {
        return "CA MARCHE";
    }

    public static void main(String[] args) {
        /* Integer test = 0;
        System.out.println(test.getClass().getName());
        try {
            // System.out.println(Class.forName("up.visulog.analyzer.ActivityUserGraphPlugin").getConstructor().newInstance().toString());

            System.out.println(Class.forName(test.getClass().getName())).getConstructor().newInstance().toString());
        } catch (Exception e) {
            e.printStackTrace();
        } */
    }
}
