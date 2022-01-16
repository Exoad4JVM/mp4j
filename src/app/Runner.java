package app;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.FlatArcDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkMediumIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatNordIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatSolarizedLightIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatVuesionIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerIJTheme;

import app.core.PropertiesReader;
import app.global.Items;

/**
 * <h1>Runner</h1>
 * <p>
 * This class makes sure everything
 * is running the same events and
 * reads the data from the save locations
 * and loads them into the following
 * windows and classes
 * <p>
 *
 * @author Jack Meng
 *
 * @since 1.1
 * @see app.global.Sources
 * @see app.Runner
 * @see app.global.Items
 * @see app.interfaces.Splash
 * @see app.interfaces.WelcomeWindow
 */

public class Runner {

  /**
   * @return String
   * @throws IOException IO is used here
   */
  public static String readInfo() throws IOException {
    if (new File(Items.items[1] + "/" + app.global.Sources.LIFEPRESERVER_PREVDIR).exists()
        || new File(Items.items[1] + "/" + app.global.Sources.LIFEPRESERVER_PREVDIR).isDirectory()) {

      BufferedReader br = new BufferedReader(
          new FileReader(app.global.Items.items[1] + "/" + app.global.Sources.LIFEPRESERVER_PREVDIR));
      StringBuilder sb = new StringBuilder();
      String line = br.readLine();
      while (line != null) {
        sb.append(line);
        line = br.readLine();
      }
      br.close();
      return sb.toString();
    }
    return ".";
  }

  public boolean run() throws IOException {
    try {
      initLAF();
    } catch (IOException e1) {
      e1.printStackTrace();
    }

    System.setProperty("flatlaf.useJetBrainsCustomDecorations", "true");
    System.setProperty("flatlaf.animation", "false");

    File apiCache = new File(Items.items[0]);
    if (!apiCache.isDirectory()) {
      apiCache.mkdir();
    }
    File mpSaves = new File(Items.items[1]);
    if (!mpSaves.isDirectory()) {
      mpSaves.mkdir();
    }

    File customs = new File(Items.items[5]);
    if (!customs.isDirectory()) {
      customs.mkdir();
    }

    File mpLogs = new File(Items.items[2]);
    if(!mpLogs.isDirectory()) {
      mpLogs.mkdir();
    }
    
    try {
      Socket socket = new Socket();
      socket.connect(new InetSocketAddress("google.com", 80), 3000);
      socket.close();
    } catch (IOException e) {
      BufferedWriter bw = new BufferedWriter(new FileWriter(app.global.Items.items[6]));
      bw.write("0");
      bw.close();
      return false;
    }
    BufferedWriter bw = new BufferedWriter(new FileWriter(app.global.Items.items[6]));
    bw.write("1");
    bw.close();
    
    return true;
  }

  private static void initLAF() throws IOException {
    try {
      PropertiesReader.generalProp();
      PropertiesReader.keyyedProp();
    } catch (IOException e) {
      e.printStackTrace();
    }
    new app.core.PropertiesReader();
    String laf = app.core.PropertiesReader.getProp("gui.defaultTheme");
    switch (laf) {
      case "material":
        FlatMaterialDarkerIJTheme.setup();
        break;
      case "onedark":
        FlatOneDarkIJTheme.setup();
        break;
      case "arcdark":
        FlatArcDarkIJTheme.setup();
        break;
      case "nord":
        FlatNordIJTheme.setup();
        break;
      case "dracula":
        FlatDraculaIJTheme.setup();
        break;
      case "gruvbox":
        FlatGruvboxDarkMediumIJTheme.setup();
        break;
      case "vuesion":
        FlatVuesionIJTheme.setup();
        break;
      case "regularlight":
        FlatLightLaf.setup();
        break;
      case "solarized":
        FlatSolarizedLightIJTheme.setup();
        break;
      default:
        FlatDarkLaf.setup();
        break;
    }
    
  }

  /**
   * @param args
   * @throws InterruptedException
   * @throws IOException
   */
  public static void main(String[] args) throws InterruptedException, IOException {
    new Runner().run();
    new app.interfaces.Splash(Items.SPLASH_SECONDS).run();
    new app.interfaces.WelcomeWindow(readInfo()).run();
  }

}