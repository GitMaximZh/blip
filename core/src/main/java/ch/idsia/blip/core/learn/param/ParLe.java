package ch.idsia.blip.core.learn.param;

import ch.idsia.blip.core.Base;
import ch.idsia.blip.core.common.BayesianNetwork;
import ch.idsia.blip.core.common.DataSet;
import ch.idsia.blip.core.utils.data.ArrayUtils;

import static ch.idsia.blip.core.utils.RandomStuff.p;
import static ch.idsia.blip.core.utils.RandomStuff.pf;

public abstract class ParLe extends Base {

    public BayesianNetwork bn;

    protected DataSet dat;

    double n_datapoints;

    public BayesianNetwork go(BayesianNetwork res, DataSet dat) {

        this.dat = dat;

            prepareBn(res);

            n_datapoints = this.dat.n_datapoints;

            // Compute potential for each variable
            for (int i = 0; i < bn.n_var; i++) {
                double[] potent;

                if (bn.parents(i).length == 0) {
                    potent = computePotentialsSimple(i);
                } else {
                    potent = computePotentials(i);
                }

                bn.l_potential_var[i] = potent;
            }

        return bn;
    }

    /*
     protected boolean checkNames(BayesianNetwork res) {
     // check that datafile and supplied structure shares the names;
     // also builds index conversion array
     if (bn.n_var != dat.n_var) {
     p("Different variable size!");
     return false;
     }

     conv = new int[bn.n_var];

     for (int thread = 0; thread < dat.n_var; thread++) {
     String nm = clean(dat.l_s_names[thread]);
     int index = -1;

     for (int tw = 0; tw < bn.n_var; tw++) {
     String nm2 = bn.name( tw);

     if (nm2.equals(clean(nm2))) {
     index = thread;
     break;
     }

     }
     if (index == -1) {
     pf(
     "Variable %s from the dataset not found in bayesian structure! \n",
     nm);
     return false;
     }
     conv[thread] = index;
     }

     // p(conv);

     return true;
     } */

    private void prepareBn(BayesianNetwork res) {

        bn = new BayesianNetwork(res.n_var);

        bn.l_parent_var = res.l_parent_var;

        int ix = 0;

        for (int i = 0; i < dat.n_var; i++) {
            String s = dat.l_s_names[i];

            bn.l_nm_var[i] = s;

            int ar = dat.l_n_arity[ix++];

            bn.l_ar_var[i] = ar;

            String[] vl = new String[ar];

            for (int j = 0; j < ar; j++) {
                vl[j] = String.format("s%d", j);
            }
            bn.l_values_var[i] = vl;

            bn.l_potential_var[i] = new double[0];
        }

    }

    protected abstract double[] computePotentials(int i);

    protected abstract double[] computePotentialsSimple(int i);

    int[] computeCardinalities(int var, int[] parents, int j) {

        int n = j;

        int[] parents_var = getParentsConf(parents, n);

        int ar = bn.arity(var);
        int[] n_ij = new int[ar];

        int[][] vl_var = this.dat.row_values[var];

        // System.out.println(var + " ... " + ar + " .... " + vl_var.length);
        if (verbose > 1 && parents_var.length < 50 )
            pf("WARNING! Variable %s, less than 50 datapoints in parent configuration! There are: %d \n", bn.name(var), parents_var.length);

        // For every variable configuration, compute the n's
        for (int v = 0; v < ar; v++) {
            n_ij[v] = ArrayUtils.intersectN(parents_var, vl_var[v]);

            if (n_ij[v] < 50)
                ; // pf("WARNING! Variable %s, less than 50 datapoints in parameter estimation! \n", bn.name(var));
        }
        return n_ij;
    }

    protected int[] getParentsConf(int[] parents, int n) {
        // Get a parents configuration
        int[] parents_var = null;

        for (int i = parents.length-1; i >= 0; i--) {

            int par = parents[i];

            // Get value for the parent in this configuration
            short val = (short) (n % bn.arity(par));

            n /= bn.arity(par);

            // Update set containing sample rows for the chosen configuration
            int[] par_var = null;
            try {
                par_var = dat.row_values[par][val];
            } catch (ArrayIndexOutOfBoundsException ex) {
                p("cia");
            }

            if (parents_var == null) {
                parents_var = par_var;
            } else {
                parents_var = ArrayUtils.intersect(parents_var, par_var);
            }
        }
        return parents_var;
    }

    public static BayesianNetwork ex(BayesianNetwork bn, DataSet dat) {
        return new ParLeBayes(10).go(bn, dat);
    }

}
