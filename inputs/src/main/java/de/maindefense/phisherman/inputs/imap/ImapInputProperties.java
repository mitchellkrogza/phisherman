package de.maindefense.phisherman.inputs.imap;

import de.maindefense.phisherman.inputs.imap.config.ImapServerProperties;
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

}
