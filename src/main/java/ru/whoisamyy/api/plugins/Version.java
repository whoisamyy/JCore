package ru.whoisamyy.api.plugins;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class Version {
    private final String version;
    private final String suffix;

    /**
     * parses given string into version string and suffix string
     * @param version version string example: "1.10.2:PREVIEW"
     */
    public Version(@NonNull String version) {
        String[] versionArr = version.split(":");
        version = versionArr[0];
        String suffix = versionArr[1];
        this.version = version;
        this.suffix = suffix;
    }
}
