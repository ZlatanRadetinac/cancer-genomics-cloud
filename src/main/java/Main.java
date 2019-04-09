import cgcli.CGCManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args){
        logger.info("Cancer genomic cloud CLI tool has been started");
        CGCManager.getInstance().start();
        logger.info("Cancer genomic cloud CLI tool has been terminated successfully");
    }
}
