/*
 * Copyright (c) 2015 Jeff Martin.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public
 * License v3.0 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * Contributors:
 *     Jeff Martin - initial API and implementation
 */

package me.dalekcraft.structureedit.util;

import java.util.Locale;

public enum OperatingSystem {
    LINUX,
    MAC,
    SOLARIS,
    WINDOWS,
    OTHER;

    private static OperatingSystem operatingSystem;

    public static OperatingSystem getOperatingSystem() {
        if (operatingSystem == null) {
            String operatingSystemName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
            if (operatingSystemName.contains("mac") || operatingSystemName.contains("darwin")) {
                operatingSystem = MAC;
            } else if (operatingSystemName.contains("win")) {
                operatingSystem = WINDOWS;
            } else if (operatingSystemName.contains("nix") || operatingSystemName.contains("nux") || operatingSystemName.contains("aix")) {
                operatingSystem = LINUX;
            } else if (operatingSystemName.contains("sunos")) {
                operatingSystem = SOLARIS;
            } else {
                operatingSystem = OTHER;
            }
        }
        return operatingSystem;
    }
}
