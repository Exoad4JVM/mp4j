package project;

import project.audio.Overseer;
import project.components.BigContainer;
import project.components.ParentPanel;
import project.components.ProcessesSchedule;
import project.components.sub_components.FileViewPanel;
import project.components.sub_components.FileViewWrapper;
import project.components.sub_components.InfoView;
import project.components.sub_components.infoview.BottomView;
import project.components.sub_components.infoview.TopView;
import project.connection.discord.DiscordRPCHandler;
import project.connection.resource.ResourceFolder;
import project.connection.resource.ResourceWriter;
import project.connection.telemetry.Out;
import project.constants.ProjectManager;
import project.constants.Size;
import strict.RuntimeConstant;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.LogManager;

/**
 * This class holds everything that will start the program.
 * It is why it's called "Main".
 * 
 * It does not handle anything else after this, such as GUI handling
 * or audio processing.
 * 
 * @author Jack Meng
 * @since 2.0
 * @see project.audio.Overseer
 * @see project.components.BigContainer
 * @see project.components.ParentPanel
 */
public class Main implements ActionListener {
  /**
   * Things we want to run on startup to ensure
   * everything regarding the file-systems are correct.
   */
  static {
    System.setProperty("file.encoding", "UTF-8");
    System.setProperty("sun.jnu.encoding", "UTF-8");

    ResourceFolder.checkResourceFolder();
    ResourceFolder.pm.open();
  }

  private BigContainer e;

  /**
   * This is the method that is called when the program is run.
   * It belongs to class Main and is not static.
   */
  public void launch() {
    System.setProperty("flatlaf.useJetBrainsCustomDecorations", "false");
    DiscordRPCHandler disch = new DiscordRPCHandler();
    disch.start();
    ParentPanel pb;
    FileViewPanel fileViewPanel = new FileViewPanel();
    Map<JComponent, String> panels = new HashMap<>();
    TopView tv = new TopView();
    Overseer overseer = new Overseer(null, fileViewPanel, tv, disch);
    BottomView bw = new BottomView(overseer);
    JSplitPane otherSide = new InfoView(tv, bw);
    otherSide.setDividerLocation(Size.HEIGHT - 100);
    fileViewPanel.getAl();
    FileViewWrapper fvw = new FileViewWrapper(this, fileViewPanel, overseer);
    JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, otherSide, fvw);
    jsp.setDividerLocation(Size.WIDTH - fvw.getWidth() - 20);

    fileViewPanel.dispatch();
    panels.put(jsp, BorderLayout.CENTER);
    pb = new ParentPanel(panels);
    e = new BigContainer(pb);
    e.run();
  }

  public static final PrintStream STDOUT = System.out;

  /**
   * This is where the program starts.
   * However there might be more main() in other classes.
   * 
   * @param args Takes nothing from arguments, however you can put, but will be
   *             ignored for now.
   */
  public static synchronized void main(String[] args) {
    Out.log("ASBOLUTE START");
    long curr = System.currentTimeMillis();
    try {
      if (ProjectManager.DISABLE_IO) {
        System.setOut(new PrintStream(new OutputStream() {
          @Override
          public void write(int arg0) throws IOException {
            // IGNORE OUTPUT
          }
        }));
        LogManager.getLogManager().reset();
      }
      ResourceFolder.checkResourceFolder();
      for (String s : ProjectManager.EXT_RSC_FOLDERS) {
        ResourceWriter.createFolder(s);
      }
      ProcessesSchedule.main();
      new Main().launch();
    } catch (Exception e) {
      e.printStackTrace();
      Date d = new Date(System.currentTimeMillis());
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
      ResourceFolder.writeLog("logs",
          "MP4J - LOG EXCEPTION | PLEASE KNOW WHAT YOU ARE DOING\nException caught time: " + df.format(d) + "\n"
              + e.getClass() + "\n" + e.toString() + "\n" +
              e.getMessage() + "\nLOCALIZED: " + e.getLocalizedMessage() + "\n" + e.getStackTrace() + "\n"
              + "Submit an issue by making a PR to the file BUGS at " + ProjectManager.GITHUB_PROJECT_URL);
    }
    Out.log("Startup tasks finished");
    Out.log("Total start up time: " + (System.currentTimeMillis() - curr) + "ms");
  }

  /**
   * Responds to the user clicking on a button to set the original width and
   * height of the
   * big-container.
   * 
   * @param arg0 ActionListener for the current big container reference.
   */
  @Override
  public void actionPerformed(ActionEvent arg0) {
    e.getBigFrame().setSize(new Dimension(Size.WIDTH, Size.HEIGHT));
  }
}
