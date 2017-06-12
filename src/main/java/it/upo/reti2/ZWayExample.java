package it.upo.reti2;

import com.github.sarxos.webcam.Webcam;
import de.fh_zwickau.informatik.sensor.IZWayApi;
import de.fh_zwickau.informatik.sensor.ZWayApiHttp;
import de.fh_zwickau.informatik.sensor.model.devices.Device;
import de.fh_zwickau.informatik.sensor.model.devices.DeviceList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//aggiunto per cam
//import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.Webcam;

//

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;


/**
 * Sample usage of the Z-Way API. It looks for all sensors and power outlets.
 * It reports the temperature and power consumption of available sensors and, then, turn power outlets on for 10 seconds.
 * <p>
 * It uses the Z-Way library for Java included in the lib folder of the project.
 *
 * @author <a href="mailto:luigi.derussis@uniupo.it">Luigi De Russis</a>
 * @version 1.0 (24/05/2017)
 * @see <a href="https://github.com/pathec/ZWay-library-for-Java">Z-Way Library on GitHub</a> for documentation about the used library
 */
public class ZWayExample {

    public static void main(String[] args) throws IOException {
        // init logger
        Logger logger = LoggerFactory.getLogger(ZWayExample.class);

        // example RaZberry IP address
        // o indirizzo locale del server che tiro su
        String ipAddress = "172.30.1.137";

        // example username and password
        String username = "admin";
        String password = "raz4reti2";

        // create an instance of the Z-Way library; all the params are mandatory (we are not going to use the remote service/id)
        IZWayApi zwayApi = new ZWayApiHttp(ipAddress, 8083, "http", username, password, 0, false, new ZWaySimpleCallback());

        // get all the Z-Wave devices
        DeviceList allDevices = zwayApi.getDevices();
        Device multisensorSF =  null;
        Device multisensorGPPersone = null;
        Device aperturaPorta = null;
        Device portaLampada = null;

        // search all sensors
        for (Device dev : allDevices.getAllDevices()) {
            if (dev.getDeviceType().equalsIgnoreCase("SensorMultilevel") || dev.getDeviceType().equalsIgnoreCase("SensorBinary"))
            {
                /*

                SENSORE LUMINESCENZA
                 */
                logger.info("Device " + dev.getNodeId() + " is a " + dev.getDeviceType());
                if(dev.getNodeId()==6 && dev.getDeviceType().equalsIgnoreCase("SensorMultilevel") &&
                        dev.getMetrics().getProbeTitle().contains("Luminiscence"))
                {
                    multisensorSF = dev;//pesco sensore luminosita
                }

                /*
                SENSORE PRESENZA
                 */

                if(dev.getNodeId()==6 && dev.getDeviceType().equalsIgnoreCase("SensorBinary") &&
                            dev.getMetrics().getProbeTitle().contains("purpose"))
                {
                    multisensorGPPersone = dev;
                    System.out.print("--------- MULTILEVEL PRESENZA : "+dev.getMetrics().getLevel()+"-----------");
                }


                // get only temperature and power consumption from available sensors
                if (dev.getProbeType().equalsIgnoreCase("temperature")) {
                    logger.info(dev.getMetrics().getProbeTitle() + " level: " + dev.getMetrics().getLevel() + " " + dev.getMetrics().getScaleTitle());
                } else if (dev.getProbeType().equalsIgnoreCase("meterElectric_watt")) {
                    logger.info(dev.getMetrics().getProbeTitle() + " level: " + dev.getMetrics().getLevel() + " " + dev.getMetrics().getScaleTitle());
                } else {
                    // get all measurements from sensors
                    logger.info(dev.getMetrics().getProbeTitle() + " level: " + dev.getMetrics().getLevel() + " uom: " + dev.getMetrics().getScaleTitle());
                }
            }
        }



        // search all power outlets
        for (Device dev : allDevices.getAllDevices()) {

            /*
            SENSORE APERTURA PORTE
             */
            if (dev.getNodeId()==13 && dev.getDeviceType().equalsIgnoreCase("sensorBinary"))
            {
                aperturaPorta = dev;
            }

                //prima con id 7 ora con id 18 holder
                if (dev.getDeviceType().equalsIgnoreCase("SwitchBinary") && dev.getNodeId()==18) {

                /* se il livello di luminosità è minore a 200 accendo la spina
                * se è maggiore la spengo
                * */
                portaLampada = dev;
                if(Double.parseDouble(multisensorSF.getMetrics().getLevel()) <200 )
                {
                    dev.on();
                }
                else{
                    dev.off();
                }
                // turn it on
                logger.debug("Device " + dev.getNodeId() + " is a " + dev.getDeviceType());

                logger.info("Turn device " + dev.getNodeId() + " on...");
            }
        }
/*
        // wait 10 seconds...
        logger.info("Waiting 10 seconds...");
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.info(String.valueOf(10 - i));
        }

*/
        // search again all power outlets


        for (Device dev : allDevices.getAllDevices()) {
            if (dev.getDeviceType().equalsIgnoreCase("SwitchBinary")) {
                logger.debug("Device " + dev.getNodeId() + " is a " + dev.getDeviceType());
                // turn it off
                logger.info("Turn device " + dev.getNodeId() + " off...");
                dev.off();

            }

        }



        //Stampiamo stato
        System.out.println("Sensore porta: "+aperturaPorta.getMetrics().getLevel());
        System.out.println("Sensore Luminosità: "+multisensorSF.getMetrics().getLevel());
        System.out.println("Sensore Prossimita: "+multisensorGPPersone.getMetrics().getLevel());

        //se sensore porta on allora scatto foto


        if(aperturaPorta.getMetrics().getLevel().equalsIgnoreCase("on") && (Double.parseDouble(multisensorSF.getMetrics().getLevel()) < 300))
        {

            Webcam webcam = Webcam.getDefault();
            if (webcam != null) {
                System.out.println("Webcam: " + webcam.getName());
            } else {
                System.out.println("No webcam detected");
            }


            webcam.open();
            ImageIO.write(webcam.getImage(), "PNG", new File("Images/prova.png"));
        }


        //sensore aperto 99
        //attaccati on

        //prova immagine




    }
}
