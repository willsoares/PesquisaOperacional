package conteiner;

import java.util.logging.Level;
import java.util.logging.Logger;

import ilog.concert.IloException;
import ilog.concert.IloLinearNumExpr;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;

public class App {

    public static void main(String[] args){
        try {
            IloCplex model = new IloCplex();
            
            //declarando variaveis de decisao
            IloNumVar[][] s = new IloNumVar[Data.n][Data.k];
            //instanciando
            for (int i = 0; i < Data.n; i++) {
                for (int j = 0; j < Data.k; j++) {
                    //s[i][j] = model.numVar(0, Data.b);
                    s[i][j] = model.intVar(0, Data.b);
                }
            }
            //expressao linear para funcaoobjetivo
            IloLinearNumExpr fo = model.linearNumExpr();
            for (int i = 0; i < Data.n; i++) {
                for (int j = 0; j < Data.k; j++) {
                    fo.addTerm(Data.l[i], s[i][j]);
                }
            }
            model.addMaximize(fo);
            
            //adicionando restricoes
            //restricoes de carga
            for (int j = 0; j < Data.k; j++) {
                //para cada container, adicionar uma inequacao
                IloLinearNumExpr restricaoCarga = model.linearNumExpr();
                for (int i = 0; i < Data.n; i++) {
                    restricaoCarga.addTerm(Data.p[i], s[i][j]);
                }
                //adicionando inequacao
                model.addLe(restricaoCarga, Data.cc);
            }
            
            //restricoes de volume
            for (int j = 0; j < Data.k; j++) {
                //para cada container, adicionar uma inequacao
                IloLinearNumExpr restricaoVolume = model.linearNumExpr();
                for (int i = 0; i < Data.n; i++) {
                    restricaoVolume.addTerm(Data.v[i], s[i][j]);
                }
                //adicionando inequacao
                model.addLe(restricaoVolume, Data.cv);
            }
            
            //restricoes de qtd item
            for (int i = 0; i < Data.n; i++) {
                IloLinearNumExpr restricaoLimite = model.linearNumExpr();
                for (int j = 0; j < Data.k; j++) {
                    restricaoLimite.addTerm(1.0, s[i][j]);
                }
                //adicionando inequacao
                model.addLe(restricaoLimite, Data.b);
            }
            
            //resolvendo o problema
            if(model.solve()){
                System.out.println("=================================");
                System.out.println(model.getStatus());
                System.out.println(model.getObjValue());
                System.out.println("=================================");
                for (int i = 0; i < Data.n; i++) {
                    for (int j = 0; j < Data.k; j++) {
                        System.out.print(model.getValue(s[i][j]) + "\t");
                    }
                    System.out.println("");
                }
            }
            else {
                System.out.println("Deu merda");
            }
            
        } catch (IloException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
