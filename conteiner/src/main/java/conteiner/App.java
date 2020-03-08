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
            var data = ReadFile("instancias/instancia_00.conteiner");

            IloCplex model = new IloCplex();

            // declarando variaveis de decisao
            IloNumVar[][] s = new IloNumVar[data.getQtdItens()][data.getQtdConteiners()];
            // instanciando
            for (int i = 0; i < data.getQtdItens(); i++) {
                for (int j = 0; j < data.getQtdConteiners(); j++) {
                    s[i][j] = model.intVar(0, data.getUsoMaximoItem());
                }
            }
            // expressao linear para funcaoobjetivo
            IloLinearNumExpr fo = model.linearNumExpr();
            for (int i = 0; i < data.getQtdItens(); i++) {
                for (int j = 0; j < data.getQtdConteiners(); j++) {
                    fo.addTerm(data.getItensLucro()[i], s[i][j]);
                }
            }
            model.addMaximize(fo);

            // adicionando restricoes
            // restricoes de carga
            for (int j = 0; j < data.getQtdConteiners(); j++) {
                // para cada container, adicionar uma inequacao
                IloLinearNumExpr restricaoCarga = model.linearNumExpr();
                for (int i = 0; i < data.getQtdItens(); i++) {
                    restricaoCarga.addTerm(data.getItensPeso()[i], s[i][j]);
                }
                // adicionando inequacao
                model.addLe(restricaoCarga, data.getConteinerCapacidade());
            }

            // restricoes de volume
            for (int j = 0; j < data.getQtdConteiners(); j++) {
                // para cada container, adicionar uma inequacao
                IloLinearNumExpr restricaoVolume = model.linearNumExpr();
                for (int i = 0; i < data.getQtdItens(); i++) {
                    restricaoVolume.addTerm(data.getItensVolume()[i], s[i][j]);
                }
                // adicionando inequacao
                model.addLe(restricaoVolume, data.getConteinerVolume());
            }

            // restricoes de qtd item
            for (int i = 0; i < data.getQtdItens(); i++) {
                IloLinearNumExpr restricaoLimite = model.linearNumExpr();
                for (int j = 0; j < data.getQtdConteiners(); j++) {
                    restricaoLimite.addTerm(1.0, s[i][j]);
                }
                // adicionando inequacao
                model.addLe(restricaoLimite, data.getUsoMaximoItem());
            }

            // resolvendo o problema
            if (model.solve()) {
                System.out.println("\nResultado:\n");
                System.out.println(model.getStatus());
                System.out.println(model.getObjValue());
                for (int i = 0; i < data.getQtdItens(); i++) {
                    System.out.print("|");
                    for (int j = 0; j < data.getQtdConteiners(); j++) {
                        System.out.print(model.getValue(s[i][j]) + "\t");
                    }
                    System.out.println("|");
                }
            } else {
                System.out.println("???????????????");
            }

        } catch (IloException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
