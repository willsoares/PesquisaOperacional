package conteiner;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.cplex.IloCplex;

public class App {

    public static void main(String[] args) {
        try {
            var file = "demo.conteiner";
            ReadFile("instancias/" + file);

            var modelo = new IloCplex();
            // modelo.setParam(IloCplex.Param.TimeLimit, 600);

            var s = new IloIntVar[Data.n][Data.k][Data.max + 1];
            for (int i = 0; i < Data.n; i++)
                for (int j = 0; j < Data.k; j++)
                    for (int m = 0; m < Data.max + 1; m++)
                        s[i][j][m] = modelo.intVar(0, 1);

            // * sum[i=1->n](sum[j=1->k](sum[m=0->b](m * Li * Sijm)))
            var fo = modelo.linearNumExpr();
            for (int i = 0; i < Data.n; i++)
                for (int j = 0; j < Data.k; j++)
                    for (int m = 0; m < Data.max + 1; m++)
                        fo.addTerm(Data.l[i] * m, s[i][j][m]);

            modelo.addMaximize(fo);

            // * restricao de carga
            for (int j = 0; j < Data.k; j++) {
                // Para cada conteiner
                var restricaoCarga = modelo.linearNumExpr();
                for (int i = 0; i < Data.n; i++) {
                    for (int m = 0; m < Data.max + 1; m++) {
                        restricaoCarga.addTerm(Data.p[i] * m, s[i][j][m]);
                    }
                }
                modelo.addLe(restricaoCarga, Data.cc);
            }

            // restricao de volume
            for (int j = 0; j < Data.k; j++) {
                var restricaoVolume = modelo.linearNumExpr();
                for (int i = 0; i < Data.n; i++) {
                    for (int m = 0; m < Data.max + 1; m++) {
                        restricaoVolume.addTerm(Data.v[i] * m, s[i][j][m]);
                    }
                }
                modelo.addLe(restricaoVolume, Data.cv);
            }

            // restrição de limite
            for (int i = 0; i < Data.n; i++) {
                // Para cada item, não pode passar da quantidade máxima
                var restricaoLimite = modelo.linearNumExpr();
                for (int j = 0; j < Data.k; j++) {
                    for (int m = 0; m < Data.max + 1; m++) {
                        restricaoLimite.addTerm(m, s[i][j][m]);
                    }
                }
                modelo.addLe(restricaoLimite, Data.max);
                // modelo.addGe(restricaoLimite, Data.min);
            }

            // restrição de limite de cada item
            for (int i = 0; i < Data.n; i++) {
                for (int j = 0; j < Data.k; j++) {
                    // para cada item, apenas uma quantidade de itens
                    var restricaoQuantidade = modelo.linearNumExpr();
                    for (int m = 0; m < Data.max + 1; m++) {
                        restricaoQuantidade.addTerm(1, s[i][j][m]);
                    }
                    modelo.addEq(restricaoQuantidade, 1);
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

    public static void ReadFile(String filename) {
        try {
            var reader = new BufferedReader(new FileReader(filename));

            var line = reader.readLine();
            Data.n = Integer.parseInt(line.substring(2));

            line = reader.readLine();
            var st = new StringTokenizer(line, "\t");
            st.nextToken();
            Data.k = Integer.parseInt(st.nextToken());
            Data.cc = Integer.parseInt(st.nextToken());
            Data.cv = Integer.parseInt(st.nextToken());

            line = reader.readLine();
            st = new StringTokenizer(line, "\t");
            st.nextToken();
            Data.min = Integer.parseInt(st.nextToken());

            line = reader.readLine();
            st = new StringTokenizer(line, "\t");
            st.nextToken();
            Data.max = Integer.parseInt(st.nextToken());

            int i = 0;
            line = reader.readLine();
            var l = new double[Data.n];
            var p = new double[Data.n];
            var v = new double[Data.n];
            while (line != null) {
                st = new StringTokenizer(line, "\t");
                st.nextToken();
                l[i] = Double.parseDouble(st.nextToken());
                p[i] = Double.parseDouble(st.nextToken());
                v[i] = Double.parseDouble(st.nextToken());
                i++;
                line = reader.readLine();
            }
            Data.l = l;
            Data.v = v;
            Data.p = p;

            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
