package org.example;
import com.fasterxml.jackson.core.JsonProcessingException;

public class Main {
    public static void main(String[] args) throws JsonProcessingException {
//        PythonInterop py = new PythonInterop();
//        System.out.println(py.getPositions());
        Adzuna adzuna = new Adzuna();
        System.out.println(adzuna.getJobs());
    }
}
