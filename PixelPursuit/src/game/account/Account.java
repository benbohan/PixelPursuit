package game.account;

public class Account {
    private String username;
    private String password;
    private int freeGold;
    private int freeDiamonds;
    private int vaultGold;
    private int vaultDiamonds;
    private double bestTime;
    private int color;       // equipped color      (0-14)
    private int cosmetic;    // equipped cosmetic   (15-27)
    private int multiplier;  // equipped multiplier (27-31)
    private long unlocks;    // unlocks digit mask  (32-digit)    ex: 00000000000000000000000000000000

    // Constructor - New Account
    public Account(String username, String password, int freeGold, int freeDiamonds, int vaultGold, 
    				int vaultDiamonds, double bestTime, int color, int cosmetic, int multiplier, long unlocks) {
        this.username = username;
        this.password = password;
        this.freeGold = freeGold;
        this.vaultGold = vaultGold;
        this.freeDiamonds = freeDiamonds;
        this.vaultDiamonds = vaultDiamonds;
        this.bestTime = bestTime;
        this.color = color;
        this.cosmetic = cosmetic;
        this.multiplier = multiplier;
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
    public int getColor()         { return color; }
    public int getCosmetic()      { return cosmetic; }
    public int getMultiplier()    { return multiplier; }
    public long getUnlocks()      { return unlocks; }

    // Setters
    public void setFreeGold(int freeGold)           { this.freeGold = freeGold; }
    public void setFreeDiamonds(int freeDiamonds)   { this.freeDiamonds = freeDiamonds; }
    public void setVaultGold(int vaultGold)         { this.vaultGold = vaultGold; }
    public void setVaultDiamonds(int vaultDiamonds) { this.vaultDiamonds = vaultDiamonds; }
    public void setBestTime(double bestTime)        { this.bestTime = bestTime; }
    public void setColor(int color)                 { this.color = color; }
    public void setCosmetic(int cosmetic)           { this.cosmetic = cosmetic; }
    public void setMultiplier(int multiplier)       { this.multiplier = multiplier; }
    public void setUnlocks(long unlocks)            { this.unlocks = unlocks; }
    
    
    // Line setup for accounts.txt storage
    public String toFileLine() {
        return username + ";" + password + ";" + freeGold + ";" + freeDiamonds + ";" + 
        		vaultGold + ";" + vaultDiamonds + ";" + bestTime + ";" + color + ";" + 
        		cosmetic + ";" + multiplier + ";" + unlocks;
    }

    // Parse from one line in "accounts.txt"
    public static Account fromFileLine(String line) {
        String[] parts = line.split(";");
        
        // Invaild account line check
        if (parts.length < 11) {
            throw new IllegalArgumentException("Invalid account line: " + line);
        }
        
        /* Format all player info (1 Line)
         * username;password;freeGold;freeDiamonds;vaultGold;vaultDiamonds;
         * bestTime;color;cosmetic;multiplier; unlocks */
        String username    = parts[0];
        String password    = parts[1];
        int freeGold       = Integer.parseInt(parts[2]);
        int freeDiamonds   = Integer.parseInt(parts[3]);
        int vaultGold      = Integer.parseInt(parts[4]);
        int vaultDiamonds  = Integer.parseInt(parts[5]);
        double bestTime    = Double.parseDouble(parts[6]);
        int color          = Integer.parseInt(parts[7]);
        int cosmetic       = Integer.parseInt(parts[8]);
        int multiplier     = Integer.parseInt(parts[9]);
        long unlocks       = Long.parseLong(parts[10]);
        
        return new Account(username, password, freeGold, freeDiamonds, vaultGold, 
        					vaultDiamonds, bestTime, color, cosmetic, multiplier, unlocks);
    }
}
