package ru.whoisamyy.api.gd.objects;

import lombok.Getter;
import ru.whoisamyy.api.gd.misc.ChallengeLootTable;
import ru.whoisamyy.api.gd.misc.Reward;
import ru.whoisamyy.api.utils.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Challenge extends Reward {
    private static Challenge instance;
    private static List<ChallengeLootTable> lootTables = getChallenges();

    public static List<ChallengeLootTable> getChallenges() {
        List<ChallengeLootTable> challenges = new ArrayList<>();

        try(Statement s = conn.createStatement()) {
            ResultSet rs = s.executeQuery("SELECT * FROM quests");

            while (rs.next()) {
                challenges.add(new ChallengeLootTable(0, rs.getInt("type"), rs.getInt("amount"), rs.getInt("reward"), rs.getString("name")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return challenges;
    }

    public static ChallengeLootTable getRandomChallengeLootTable() {
        return lootTables.get(
                ((int)(Math.random()*lootTables.size()-1))
        );
    }

    public String toString(int userID, String chk, String udid) {
        if (lootTables.size()<3) lootTables = getChallenges();

        ChallengeLootTable[] chals = new ChallengeLootTable[]{getRandomChallengeLootTable(), getRandomChallengeLootTable(), getRandomChallengeLootTable()};
        StringBuilder sb = new StringBuilder();

        int chkNum = Integer.parseInt(new String(
                        Utils.GJP.cyclicXOR(
                                Utils.base64UrlSafeDecode(
                                        chk.substring(5).getBytes()
                                ).getBytes(), "19847".getBytes()
                        )
                )
        );

        sb.append(rs).append(":").append(userID).append(":").append(chkNum).append(":").append(udid).append(":").append(userID).append(":").append(timeLeft).append(":");
        for (ChallengeLootTable c : chals) {
            sb.append(c.unknown()).append(",");
            sb.append(c.itemType()).append(",");
            sb.append(c.amount()).append(",");
            sb.append(c.diamonds()).append(",");
            sb.append(c.name()).append(":");
        }
        sb.deleteCharAt(sb.length()-1);
        String ret = Utils.base64UrlSafeEncode(Utils.GJP.cyclicXOR(sb.toString().getBytes(), "19847".getBytes()));
        ret+="|"+Utils.SHA1(ret, "oC36fpYaPtdg");
        return ret;
    }

    public static Challenge getInstance() {
        if (instance == null)
            instance = new Challenge();
        return instance;
    }
}