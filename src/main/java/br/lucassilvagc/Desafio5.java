/*
Desafio 05 - Os Programadores
Lucas Silva - github.com/lucassilvagc

Instruções para Execução:

1) javac -d . Desafio5.java
2) java br.lucassilvagc.Desafio5 [path_para_json]
*/

package br.lucassilvagc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.w3c.dom.ls.LSOutput;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Desafio5 {

    static List<Funcionario> funcionarios = new LinkedList<>();
    static String[] types = {"funcionarios", "areas"};
    static List<Area> areas = new LinkedList<>();
    static ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        System.out.println("Procurando pelo arquivo " + args[0] + "...");
        parseJson(getJsonParser(args[0]));
        globalSalary();
        areaSalary();
        areaEmployees();
        lastNameSalary();
    }

    private static JsonNode getJsonParser(String fileLocation) {
        JsonNode jsonNode = null;
        try {
            jsonNode = mapper.readTree(new File(fileLocation));
        } catch (IOException ex) {
            System.out.println("Erro: " + ex.getLocalizedMessage());
        }
        return jsonNode;
    }

    private static void parseJson(JsonNode jsonNode) {
        for (String type : types) {
            JsonNode array = jsonNode.get(type);
            for (int i = 0; i < array.size(); i++) {
                switch (type) {
                    case "areas":
                        Area area = new Area();
                        area.setCodigo(array.get(i).get("codigo").asText());
                        area.setNome(array.get(i).get("nome").asText());
                        areas.add(area);
                        break;
                    case "funcionarios":
                        Funcionario funcionario = new Funcionario();
                        funcionario.setId(array.get(i).get("id").asInt());
                        funcionario.setNome(array.get(i).get("nome").asText());
                        funcionario.setSobrenome(array.get(i).get("sobrenome").asText());
                        funcionario.setSalario(array.get(i).get("salario").asDouble());
                        funcionario.setArea(array.get(i).get("area").asText());
                        funcionarios.add(funcionario);
                        break;
                }
            }
        }
    }

    private static void globalSalary() {
        DecimalFormat df = new DecimalFormat("#.00");
        Double min = funcionarios.stream().min(Comparator.comparingDouble(Funcionario::getSalario)).orElseThrow(NoSuchElementException::new).getSalario();
        Double max = funcionarios.stream().max(Comparator.comparingDouble(Funcionario::getSalario)).orElseThrow(NoSuchElementException::new).getSalario();
        funcionarios.stream().filter(f -> (f.getSalario()).equals(max)).forEach(f -> System.out.println("global_max|" + f.getNome() + " " + f.getSobrenome() + "|" + df.format(f.getSalario())));
        funcionarios.stream().filter(f -> (f.getSalario()).equals(min)).forEach(f -> System.out.println("global_min|" + f.getNome() + " " + f.getSobrenome() + "|" + df.format(f.getSalario())));
        System.out.println("global_avg|" + df.format(funcionarios.stream().mapToDouble(Funcionario::getSalario).average().orElseThrow(NoSuchElementException::new)));
    }

    private static void areaSalary() {
        DecimalFormat df = new DecimalFormat("#.00");
        for (Area area : areas) {
            Double min = funcionarios.stream().filter(f -> (f.getArea()).equals(area.getCodigo())).min(Comparator.comparingDouble(Funcionario::getSalario)).orElseThrow(NoSuchElementException::new).getSalario();
            Double max = funcionarios.stream().filter(f -> (f.getArea()).equals(area.getCodigo())).max(Comparator.comparingDouble(Funcionario::getSalario)).orElseThrow(NoSuchElementException::new).getSalario();
            funcionarios.stream().filter(f -> (f.getArea()).equals(area.getCodigo())).filter(f -> (f.getSalario().equals(max)))
                    .forEach(f -> System.out.println("area_max|" + area.getNome() + "|" +
                            f.getNome() + " " + f.getSobrenome() + "|" + df.format(f.getSalario())));
            funcionarios.stream().filter(f -> (f.getArea()).equals(area.getCodigo())).filter(f -> (f.getSalario().equals(min)))
                    .forEach(f -> System.out.println("area_min|" + area.getNome() + "|" +
                            f.getNome() + " " + f.getSobrenome() + "|" + df.format(f.getSalario())));

            System.out.println("area_avg|" + df.format(funcionarios.stream().mapToDouble(Funcionario::getSalario).average().orElseThrow(NoSuchElementException::new)));

        }
    }

    private static void areaEmployees() {
        Map<String, Long> areaMap = new HashMap<>();

        for (Area area : areas) {
            areaMap.put(area.getNome(), funcionarios.stream().filter(c -> c.getArea().equals(area.getCodigo())).count());
        }
        Long maxValue = areaMap.values().stream().sorted().max(Long::compareTo).get();
        Long minValue = areaMap.values().stream().sorted().min(Long::compareTo).get();

        areaMap.forEach((areaName, value) -> {
            if (value.equals(maxValue)) {
                System.out.println("most_employees|" + areaName + "|" + value);
            } else if (value.equals(minValue)) {
                System.out.println("least_employees|" + areaName + "|" + value);
            }
        });
    }

    private static void lastNameSalary() {
        DecimalFormat df = new DecimalFormat("#.00");
        for (Funcionario funcionario: funcionarios) {
            Double max = funcionarios.stream().filter(f -> (f.getSobrenome()).equals(funcionario.getSobrenome())).max(Comparator.comparingDouble(Funcionario::getSalario)).orElseThrow(NoSuchElementException::new).getSalario();
            funcionarios.stream().filter(f -> (f.getSobrenome()).equals(funcionario.getSobrenome())).filter(f -> (f.getSalario().equals(max)))
                    .forEach(f -> System.out.println("last_name_max|" + funcionario.getSobrenome() + "|" +
                            f.getNome() + "|" + df.format(f.getSalario())));
        }
    }

}

class Area {

    String codigo;

    String nome;

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

}

class Funcionario {

    Integer id;

    String nome;

    String sobrenome;

    Double salario;

    String area;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public Double getSalario() {
        return salario;
    }

    public void setSalario(Double salario) {
        this.salario = salario;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

}

