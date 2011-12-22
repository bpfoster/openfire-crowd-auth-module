/*
 * Copyright (C) 2011 Ben Foster
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package net.fosterzor.openfire.crowd;


import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * User: bpfoster
 * Date: 5/25/11
 * Time: 2:32 PM
 * To change this template use File | Settings | File Templates.
 * http://www.igniterealtime.org/builds/openfire/docs/latest/documentation/plugin-dev-guide.html
 */
public class CrowdAuthPlugin implements Plugin {
    //JiveGlobals.getProperty(String)
    
    @Override
    public void initializePlugin(PluginManager manager, File pluginDirectory) {
        // TODO: Auto-generated method

    }

    @Override
    public void destroyPlugin() {
        // TODO: Auto-generated method

    }
}
