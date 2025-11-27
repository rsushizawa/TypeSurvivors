package TypeSurvivors.Model;

import java.util.ArrayList;
import java.util.List;

import static TypeSurvivors.Model.CsvReader.readCsv;

public class teste {
    public static void main(String[] args) {
        CsvReader reader = new CsvReader();
        List<String[]> words = new ArrayList<>();
        words = readCsv("src/TypeSurvivors/Model/dict.csv");

        for (String[] palavra : words){
            System.out.println(palavra);
        }


    }
}
