package de.maindefense.phisherman.inputs.imap;

import java.util.ArrayList;
import java.util.List;

public class ImapInputProperties {

  private List<ImapServerProperties> servers = new ArrayList<>();

  public List<ImapServerProperties> getServers() {
    return servers;
  }

  public void setServers(List<ImapServerProperties> servers) {
    this.servers = servers;
  }

  public static class ImapServerProperties {

    private String hostname;
    private int port;
    private String username;
    private String password;
    private String folder = "INBOX";

    public String getHostname() {
      return hostname;
    }

    public void setHostname(String hostname) {
      this.hostname = hostname;
    }

    public int getPort() {
      return port;
    }

    public void setPort(int port) {
      this.port = port;
    }

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getPassword() {
      return password;
    }

    public void setPassword(String password) {
      this.password = password;
    }


    /**
     * Gets the folder to be fetched. Defaults to {@code INBOX}
     *
     * @return the folder to be fetched
     */
    public String getFolder() {
      return folder;
    }

    public void setFolder(String folder) {
      this.folder = folder;
    }

    /**
     * Gets the refresh interval in seconds. Defaults to 60
     *
     * @return the refresh interval in seconds
     */

    @Override
    public String toString() {
      return "ImapServerProperties [hostname=" + hostname + ", port=" + port + ", username="
          + username + "]";
    }

  }
}
