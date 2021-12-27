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

import java.util.Locale;

public enum Os {
    LINUX,
    MAC,
    SOLARIS,
    WINDOWS,
    OTHER;

    private static Os os;

    public static Os getOs() {
        if (os == null) {
            String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
            if (osName.contains("mac") || osName.contains("darwin")) {
                os = MAC;
            } else if (osName.contains("win")) {
                os = WINDOWS;
            } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
                os = LINUX;
            } else if (osName.contains("sunos")) {
                os = SOLARIS;
            } else {
                os = OTHER;
            }
        }
        return os;
    }
}
