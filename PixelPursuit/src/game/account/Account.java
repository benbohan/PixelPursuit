package game.account;

/**
 * Player account data:
 *  - Stores login credentials, currencies, and cosmetic selections.
 *  - Tracks best run time and a bitmask of unlocks for colors/cosmetics/multipliers.
 *  - Can be serialized to and from a single line in accounts.txt.
 */
public class Account {

    // ---------- FIELDS ----------

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
    private long unlocks;    // unlocks mask

    // ---------- CONSTRUCTORS ----------

    // Account - Creates a new account with full currency, cosmetic, and unlock state
    public Account(String username,
                   String password,
                   int freeGold,
                   int freeDiamonds,
                   int vaultGold,
                   int vaultDiamonds,
                   double bestTime,
                   int color,
                   int cosmetic,
                   int multiplier,
                   long unlocks) {
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

    // ---------- GETTERS ----------

    // getUsername - Returns this account's username
    public String getUsername()   { return username; }

    // getPassword - Returns this account's password
    public String getPassword()   { return password; }

    // getFreeGold - Returns gold currently in the free (spendable) balance
    public int getFreeGold()      { return freeGold; }

    // getFreeDiamonds - Returns diamonds currently in the free balance
    public int getFreeDiamonds()  { return freeDiamonds; }

    // getVaultGold - Returns gold stored in the vault
    public int getVaultGold()     { return vaultGold; }

    // getVaultDiamonds - Returns diamonds stored in the vault
    public int getVaultDiamonds() { return vaultDiamonds; }

    // getBestTime - Returns this account's best run time in seconds
    public double getBestTime()   { return bestTime; }

    // getColor - Returns the equipped color ID
    public int getColor()         { return color; }

    // getCosmetic - Returns the equipped cosmetic ID
    public int getCosmetic()      { return cosmetic; }

    // getMultiplier - Returns the equipped multiplier ID
    public int getMultiplier()    { return multiplier; }

    // getUnlocks - Returns the unlock bitmask for cosmetics and multipliers
    public long getUnlocks()      { return unlocks; }

    // ---------- SETTERS ----------

    // setFreeGold - Updates the free gold balance
    public void setFreeGold(int freeGold)           { this.freeGold = freeGold; }

    // setFreeDiamonds - Updates the free diamond balance
    public void setFreeDiamonds(int freeDiamonds)   { this.freeDiamonds = freeDiamonds; }

    // setVaultGold - Updates the vault gold balance
    public void setVaultGold(int vaultGold)         { this.vaultGold = vaultGold; }

    // setVaultDiamonds - Updates the vault diamond balance
    public void setVaultDiamonds(int vaultDiamonds) { this.vaultDiamonds = vaultDiamonds; }

    // setBestTime - Updates the best run time in seconds
    public void setBestTime(double bestTime)        { this.bestTime = bestTime; }

    // setColor - Sets the equipped color ID
    public void setColor(int color)                 { this.color = color; }

    // setCosmetic - Sets the equipped cosmetic ID
    public void setCosmetic(int cosmetic)           { this.cosmetic = cosmetic; }

    // setMultiplier - Sets the equipped multiplier ID
    public void setMultiplier(int multiplier)       { this.multiplier = multiplier; }

    // setUnlocks - Updates the unlock bitmask
    public void setUnlocks(long unlocks)            { this.unlocks = unlocks; }

    // ---------- PERSISTENCE ----------

    // toFileLine - Serializes this account as a single ';'-separated line for accounts.txt
    public String toFileLine() {
        return String.join(";",
                username,
                password,
                String.valueOf(freeGold),
                String.valueOf(freeDiamonds),
                String.valueOf(vaultGold),
                String.valueOf(vaultDiamonds),
                String.valueOf(bestTime),
                String.valueOf(color),
                String.valueOf(cosmetic),
                String.valueOf(multiplier),
                String.valueOf(unlocks)
        );
    }

    // fromFileLine - Parses an Account from a single line in accounts.txt
    public static Account fromFileLine(String line) {
        String[] parts = line.split(";");

        // Invalid account line check
        if (parts.length < 11) {
            throw new IllegalArgumentException("Invalid account line: " + line);
        }

        /* Format (one line per player):
         * username;password;freeGold;freeDiamonds;vaultGold;vaultDiamonds;
         * bestTime;color;cosmetic;multiplier;unlocks
         */
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

        return new Account(username, password,
                freeGold, freeDiamonds,
                vaultGold, vaultDiamonds,
                bestTime, color, cosmetic, multiplier, unlocks);
    }
}
