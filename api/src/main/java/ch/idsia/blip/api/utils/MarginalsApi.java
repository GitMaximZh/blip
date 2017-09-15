package ch.idsia.blip.api.utils;


import ch.idsia.blip.api.Api;
import ch.idsia.blip.core.common.DataSet;
import ch.idsia.blip.core.utils.other.Marginals;
import org.kohsuke.args4j.Option;

import java.io.Writer;
import java.util.logging.Logger;

import static ch.idsia.blip.core.utils.other.RandomStuff.getDataSet;
import static ch.idsia.blip.core.utils.other.RandomStuff.getWriter;


public class MarginalsApi extends Api {

    private static final Logger log = Logger.getLogger(
            MarginalsApi.class.getName());

    @Option(name = "-o", required = true, usage = "Result output")
    protected String ph_out;

    @Option(name = "-d", required = true, usage = "Dataset path")
    protected String ph_dat;

    public static void main(String[] args) {
        defaultMain(args, new MarginalsApi());
    }

    @Override
    public void exec() throws Exception {
        DataSet dat_rd = getDataSet(ph_dat);
        Writer wr = getWriter(ph_out);

        Marginals.ex(dat_rd, wr);
        wr.close();
    }
}
