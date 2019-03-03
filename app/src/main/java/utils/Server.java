package utils;

public class Server {

    private boolean isPassworded;
    private String ip;
    private String serverName;
    private int playerCount;

    public Server(boolean isPassworded, String ip, String serverName, int playerCount) {
        this.isPassworded = isPassworded;
        this.ip = ip;
        this.serverName = serverName;
        this.playerCount = playerCount;
    }

    public boolean getPassworded() {
        return isPassworded;
    }

    public String getip() {
        return ip;
    }

    public String getserverName() {
        return serverName;
    }

    public int getplayerCount() {
        return playerCount;
    }
}