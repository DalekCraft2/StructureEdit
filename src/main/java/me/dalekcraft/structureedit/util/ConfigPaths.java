/*******************************************************************************
 * Copyright (c) 2015 Jeff Martin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * Contributors:
 *     Jeff Martin - initial API and implementation
 ******************************************************************************/

package me.dalekcraft.structureedit.util;

import java.nio.file.Path;

public class ConfigPaths {

    public static Path getConfigFilePath(String name) {
        String fileName = Os.getOs() == Os.LINUX ? String.format("%src", name) : String.format("%s", name);
        return getConfigPathRoot().resolve(fileName);
    }

    public static Path getConfigPathRoot() {
        switch (Os.getOs()) {
            case LINUX:
                String configHome = System.getenv("XDG_CONFIG_HOME");
                if (configHome == null) {
                    return getUserHomeUnix().resolve(".config");
                }
                return Path.of(configHome);
            case MAC:
                return getUserHomeUnix().resolve("Library").resolve("Application Support");
            case WINDOWS:
                return Path.of(System.getenv("LOCALAPPDATA"));
            default:
                return Path.of(System.getProperty("user.dir"));
        }
    }

    private static Path getUserHomeUnix() {
        String userHome = System.getenv("HOME");
        if (userHome == null) {
            userHome = System.getProperty("user.dir");
        }
        return Path.of(userHome);
    }
}
