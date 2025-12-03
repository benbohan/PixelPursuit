package game.account;

public class Account {
    private String username;
    private String password;
    private int freeGold;
    private int freeDiamonds;
    private int vaultGold;
    private int vaultDiamonds;
    private double bestTime;
    private long color;    // equipped color (0-15)
    private long cosmetic; // equipped cosmetic (0-15)
    private long unlocks;  // unlocked cosmetics bit mask

    // Constructor - New Account
    public Account(String username, String password, int freeGold, int freeDiamonds, int vaultGold, 
    				int vaultDiamonds, double bestTime, long color, long cosmetic, long unlocks) {
        this.username = username;
        this.password = password;
        this.freeGold = freeGold;
        this.vaultGold = vaultGold;
        this.freeDiamonds = freeDiamonds;
        this.vaultDiamonds = vaultDiamonds;
        this.bestTime = bestTime;
        this.color = color;
        this.cosmetic = cosmetic;
        this.unlocks = unlocks;
    }
    
    // Getters
    public String getUsername()   { return username; }
    public String getPassword()   { return password; }
    public int getFreeGold()      { return freeGold; }
    public int getFreeDiamonds()  { return freeDiamonds; }
    public int getVaultGold()     { return vaultGold; }
    public int getVaultDiamonds() { return vaultDiamonds; }
    public double getBestTime()   { return bestTime; }
    public long getColor()        { return color; }
    public long getCosmetic()     { return cosmetic; }
    public long getUnlocks()      { return unlocks; }

    // Setters
    public void setFreeGold(int freeGold)           { this.freeGold = freeGold; }
    public void setFreeDiamonds(int freeDiamonds)   { this.freeDiamonds = freeDiamonds; }
    public void setVaultGold(int vaultGold)         { this.vaultGold = vaultGold; }
    public void setVaultDiamonds(int vaultDiamonds) { this.vaultDiamonds = vaultDiamonds; }
    public void setBestTime(double bestTime)        { this.bestTime = bestTime; }
    public void setColor(long color)                { this.color = color; }
    public void setCosmetic(long cosmetic)          { this.cosmetic = cosmetic; }
    public void setUnlocks(long unlocks)            { this.unlocks = unlocks; }
    
    
    // Line setup for accounts.txt storage
    public String toFileLine() {
        return username + ";" + password + ";" + freeGold + ";" + freeDiamonds + ";" + 
        		vaultGold + ";" + vaultDiamonds + ";" + bestTime + ";" + color + ";" + 
        		cosmetic + ";" + unlocks;
    }

    // Parse from one line in "accounts.txt"
    public static Account fromFileLine(String line) {
        String[] parts = line.split(";");
        
        // Initialize values
        int freeGold = 0;
        int vaultGold = 0;
        int freeDiamonds = 0;
        int vaultDiamonds = 0;
        double bestTime = 0.0;
        long color = 0L;
        long cosmetic = 0L;
        long unlocks = 0L;
        
        /* Format all player info (1 Line)
         * username;password;
         * freeGold;freeDiamonds;vaultGold;vaultDiamonds;
         * bestTime;color;cosmetic;unlocks
         */
        String username = parts[0];
        String password = parts[1];
        freeGold      = Integer.parseInt(parts[2]);
        freeDiamonds  = Integer.parseInt(parts[3]);
        vaultGold     = Integer.parseInt(parts[4]);
        vaultDiamonds = Integer.parseInt(parts[5]);
        bestTime      = Double.parseDouble(parts[6]);
        color         = Long.parseLong(parts[7]);
        cosmetic      = Long.parseLong(parts[8]);
        unlocks       = Long.parseLong(parts[9]);
        
        return new Account(username, password, freeGold, freeDiamonds, vaultGold, 
        					vaultDiamonds, bestTime, color, cosmetic, unlocks);
    }
}
