package ru.whoisamyy.api.gd.objects;

import ru.whoisamyy.api.gd.misc.ChallengeLootTable;
import ru.whoisamyy.api.gd.misc.ChestLootTable;
import ru.whoisamyy.api.gd.misc.Reward;
import ru.whoisamyy.api.utils.Utils;
import ru.whoisamyy.api.utils.enums.Shard;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Chest extends Reward {
    public static Chest instance;
    private static List<ChestLootTable> lootTables = new ArrayList<>();

    public static List<ChestLootTable> getChestLootTables() {
        List<ChestLootTable> lootTables = new ArrayList<>();

        try(Statement s = conn.createStatement()) {
            ResultSet rs = s.executeQuery("SELECT * FROM chests");

            while (rs.next()) {
                lootTables.add(new ChestLootTable(rs.getInt("orbs"), rs.getInt("diamonds"), Shard.intToShard(rs.getInt("shard")), rs.getInt("hasKey")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lootTables;
    }

    public static ChestLootTable getRandomChestLootTable() {
        return lootTables.get(
                ((int)(Math.random()*lootTables.size()-1))
        );
    }

    public String toString(int userID, int rewardType, String chk, String udid) {
        if (lootTables.size()<3) lootTables = getChestLootTables();

        ChestLootTable[] chests = new ChestLootTable[]{getRandomChestLootTable(), getRandomChestLootTable()};
        StringBuilder sb = new StringBuilder();

        int chkNum = Integer.parseInt(new String(
                Utils.GJP.cyclicXOR(
                        Utils.base64UrlSafeDecode(
                                chk.substring(5).getBytes()
                        ).getBytes(), "59182".getBytes()
                )
            )
        );

        sb.append(":").append(userID).append(":").append(chkNum).append(":").append(udid).append(":").append(userID).append(":");
        for (ChestLootTable c : chests) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(timeLeft).append(":");
            sb2.append(c.orbs()).append(",");
            sb2.append(c.diamonds()).append(",");
            sb2.append(c.shard().val).append(",");
            sb2.append(c.key()).append(":");
            sb.append(sb2).append(sb2.length()).append(":");
        }
        sb.append(rewardType);
        String ret = Utils.base64UrlSafeEncode(Utils.GJP.cyclicXOR(sb.toString().getBytes(), "59182".getBytes()));
        ret+="|"+Utils.SHA1(ret, "pC26fpYaQCtg");

        return ret;
    }

    public static Chest getInstance() {
        if (instance == null)
            instance = new Chest();
        return instance;
    }
}
