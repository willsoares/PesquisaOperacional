package conteiner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class App {
    public static Data ReadFile(String filename) {
        try {
            var reader = new BufferedReader(new FileReader(filename));
            var data = new Data();

            var line = reader.readLine();
            data.setQtdItens(Integer.parseInt(line.substring(2)));

            line = reader.readLine();
            var st = new StringTokenizer(line, "\t");
            st.nextToken();
            data.setQtdConteiners(Integer.parseInt(st.nextToken()));
            data.setConteinerCapacidade(Integer.parseInt(st.nextToken()));
            data.setConteinerVolume(Integer.parseInt(st.nextToken()));

            line = reader.readLine();
            st = new StringTokenizer(line, "\t");
            st.nextToken();
            data.setUsoMaximoItem(Integer.parseInt(st.nextToken()));

            int i = 0;
            line = reader.readLine();
            var l = new double[data.getQtdItens()];
            var p = new double[data.getQtdItens()];
            var v = new double[data.getQtdItens()];
            while (line != null) {
                st = new StringTokenizer(line, "\t");
                st.nextToken();
                l[i] = Double.parseDouble(st.nextToken());
                p[i] = Double.parseDouble(st.nextToken());
                v[i] = Double.parseDouble(st.nextToken());
                i++;
                line = reader.readLine();
            }
            data.setItensLucro(l);
            data.setItensPeso(p);
            data.setItensVolume(v);

            reader.close();
            return data;
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public static void main(String[] args) {
        try {
            var file = "instancia_02.conteiner";
            var data = ReadFile("instancias/" + file);

            IloCplex modelo = new IloCplex();

            // somatorio
            IloNumVar[][] s = new IloNumVar[data.getQtdItens()][data.getQtdConteiners()];
            for (int i = 0; i < data.getQtdItens(); i++) {
                for (int j = 0; j < data.getQtdConteiners(); j++) {
                    // s[i][j] = modelo.numVar(0, data.getUsoMaximoItem());
                    s[i][j] = modelo.intVar(0, data.getUsoMaximoItem());
                }
            }

            // expressão linear
            IloLinearNumExpr fo = modelo.linearNumExpr();
            for (int i = 0; i < data.getQtdItens(); i++) {
                for (int j = 0; j < data.getQtdConteiners(); j++) {
                    fo.addTerm(data.getItensLucro()[i], s[i][j]);
                }
            }

            // maximizar
            modelo.addMaximize(fo);
            // limitando o programa para 5 minutos
            modelo.setParam(IloCplex.Param.TimeLimit, 600);
            // adcionar restrições
            // restrição de carga

            for (int j = 0; j < data.getQtdConteiners(); j++) {// j=1..k
                // para cada container adicionar uma inequação
                IloLinearNumExpr restricaoCarga = modelo.linearNumExpr();
                for (int i = 0; i < data.getQtdItens(); i++) {
                    restricaoCarga.addTerm(data.getItensPeso()[i], s[i][j]);
                }
                // adicionar inequação
                modelo.addLe(restricaoCarga, data.getConteinerCapacidade());
            }

            // restrição de volume
            for (int j = 0; j < data.getQtdConteiners(); j++) {
                IloLinearNumExpr restricaoVolume = modelo.linearNumExpr();
                for (int i = 0; i < data.getQtdItens(); i++) {
                    restricaoVolume.addTerm(data.getItensVolume()[i], s[i][j]);
                }
                modelo.addLe(restricaoVolume, data.getConteinerVolume());
            }
            // restrição de limite
            for (int i = 0; i < data.getQtdItens(); i++) {
                IloLinearNumExpr restricaoLimite = modelo.linearNumExpr();
                for (int j = 0; j < data.getQtdConteiners(); j++) {
                    restricaoLimite.addTerm(1.0, s[i][j]);
                }
                modelo.addLe(restricaoLimite, data.getUsoMaximoItem());
            }
            // restrição de limite de cada item
            for (int i = 0; i < data.getQtdItens(); i++) {
                for (int j = 0; j < data.getQtdConteiners(); j++) {
                    modelo.addLe(s[i][j], data.getUsoMaximoItem());
                    modelo.addGe(s[i][j], 0.0);
                }
            }
            if (modelo.solve()) {
                System.out.println("----------");
                System.out.println(modelo.getStatus());
                System.out.println(modelo.getObjValue());
                System.out.println("----------");

                for (int i = 0; i < data.getQtdItens(); i++) {
                    for (int j = 0; j < data.getQtdConteiners(); j++) {
                        System.out.print(modelo.getValue(s[i][j]) + "\t");
                    }
                    System.out.println();
                }
            } else {
                System.out.println("Erro");
            }

        } catch (IloException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
