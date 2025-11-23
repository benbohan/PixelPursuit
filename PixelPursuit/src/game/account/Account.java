package game.account;

public class Account {
    private String username;
    private String password;
    private int freeGold;   // gold at risk in current/last run
    private int vaultGold;  // banked gold
    private double bestTime; // best time survived (seconds)

    public Account(String username, String password,
                   int freeGold, int vaultGold, double bestTime) {
        this.username = username;
        this.password = password;
        this.freeGold = freeGold;
        this.vaultGold = vaultGold;
        this.bestTime = bestTime;
    }

    public String getUsername()  { return username; }
    public String getPassword()  { return password; }
    public int getFreeGold()     { return freeGold; }
    public int getVaultGold()    { return vaultGold; }
    public double getBestTime()  { return bestTime; }

    public void setFreeGold(int freeGold)   { this.freeGold = freeGold; }
    public void setVaultGold(int vaultGold) { this.vaultGold = vaultGold; }
    public void setBestTime(double bestTime){ this.bestTime = bestTime; }

    // Convert to one line for the file
    public String toFileLine() {
        return username + ";" + password + ";" +
               freeGold + ";" + vaultGold + ";" + bestTime;
    }

    // Parse from one line in the file, supporting old and new formats
    public static Account fromFileLine(String line) {
        String[] parts = line.split(";");
        String username = parts[0];
        String password = parts[1];

        int freeGold = 0;
        int vaultGold = 0;
        double bestTime = 0.0;

        if (parts.length >= 5) {
            // new format: user;pass;free;vault;bestTime
            freeGold  = Integer.parseInt(parts[2]);
            vaultGold = Integer.parseInt(parts[3]);
            bestTime  = Double.parseDouble(parts[4]);
        } else if (parts.length == 4) {
            // old format: user;pass;totalGold;bestTime
            int totalGold = Integer.parseInt(parts[2]);
            vaultGold = totalGold;
            bestTime  = Double.parseDouble(parts[3]);
        }

        return new Account(username, password, freeGold, vaultGold, bestTime);
    }
}
