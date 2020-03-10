package conteiner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class App {

    public static void main(String[] args) {
        try {
            var file = "instancia_02.conteiner";
            var data = ReadFile("instancias/" + file);

            var modelo = new IloCplex();
            modelo.setParam(IloCplex.Param.TimeLimit, 600);

            var solucao = new IloNumVar[data.getQtdItens()][data.getQtdConteiners()][data.getUsoMaximoItem() + 1];
            for (int i = 0; i < data.getQtdItens(); i++)
                for (int j = 0; j < data.getQtdConteiners(); j++)
                    for (int m = 0; m < data.getUsoMaximoItem() + 1; m++)
                        solucao[i][j][m] = modelo.boolVar();

            // sum[i=1->n](sum[j=1->k](sum[m=0->b](m * Li * Sijm)))
            var funcaoObjetivo = modelo.linearNumExpr();
            for (int i = 0; i < data.getQtdItens(); i++)
                for (int j = 0; j < data.getQtdConteiners(); j++)
                    for (int m = 0; m < data.getUsoMaximoItem() + 1; m++)
                        funcaoObjetivo.addTerm(data.getItensLucro()[i] * m, solucao[i][j][m]);

            modelo.addMaximize(funcaoObjetivo);

            // restricao de carga
            for (int j = 0; j < data.getQtdConteiners(); j++) {
                // Para cada conteiner
                var restricaoCarga = modelo.linearNumExpr();
                for (int i = 0; i < data.getQtdItens(); i++) {
                    for (int m = 0; m < data.getUsoMaximoItem() + 1; m++) {
                        restricaoCarga.addTerm(data.getItensPeso()[i] * m, solucao[i][j][m]);
                    }
                }
                modelo.addLe(restricaoCarga, data.getConteinerCapacidade());
            }

            // restricao de volume
            for (int j = 0; j < data.getQtdConteiners(); j++) {
                var restricaoVolume = modelo.linearNumExpr();
                for (int i = 0; i < data.getQtdItens(); i++) {
                    for (int m = 0; m < data.getUsoMaximoItem() + 1; m++) {
                        restricaoVolume.addTerm(data.getItensVolume()[i] * m, solucao[i][j][m]);
                    }
                }
                modelo.addLe(restricaoVolume, data.getConteinerVolume());
            }

            // restrição de limite
            for (int i = 0; i < data.getQtdItens(); i++) {
                // Para cada item, não pode passar da quantidade máxima
                var restricaoLimite = modelo.linearNumExpr();
                for (int j = 0; j < data.getQtdConteiners(); j++) {
                    for (int m = 0; m < data.getUsoMaximoItem() + 1; m++) {
                        restricaoLimite.addTerm(1 * m, solucao[i][j][m]);
                    }
                }
                modelo.addLe(restricaoLimite, data.getUsoMaximoItem());
            }

            // restrição de limite de cada item
            for (int i = 0; i < data.getQtdItens(); i++) {
                for (int j = 0; j < data.getQtdConteiners(); j++) {
                    // para cada item, apenas uma quantidade de itens
                    var restricaoQuantidade = modelo.linearNumExpr();
                    for (int m = 0; m < data.getUsoMaximoItem() + 1; m++) {
                        restricaoQuantidade.addTerm(1.0, solucao[i][j][m]);
                    }
                    modelo.addEq(restricaoQuantidade, 1.0);
                }
            }

            if (modelo.solve()) {
                System.out.println("----------");
                System.out.println(modelo.getStatus());
                System.out.println(modelo.getObjValue());
                System.out.println("----------");
            } else {
                System.out.println("Erro");
            }

        } catch (IloException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

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

}
