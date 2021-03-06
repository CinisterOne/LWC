/**
 * This file is part of LWC (https://github.com/Hidendra/LWC)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.griefcraft.modules.admin;

import com.griefcraft.lwc.LWC;
import com.griefcraft.scripting.JavaModule;
import com.griefcraft.scripting.event.LWCCommandEvent;
import com.griefcraft.util.StringUtils;
import org.bukkit.command.CommandSender;

public class BaseAdminModule extends JavaModule {

    @Override
    public void onCommand(LWCCommandEvent event) {
        if(event.isCancelled()) {
            return;
        }

        if (!event.hasFlag("a", "admin")) {
            return;
        }

        LWC lwc = event.getLWC();
        CommandSender sender = event.getSender();
        String[] args = event.getArgs();

        if (args.length == 0) {
            if (lwc.isAdmin(sender)) {
                lwc.sendLocale(sender, "help.admin");
            }

            event.setCancelled(true);
            return;
        } else if (args.length > 0) {
            // check for permissions
            if (!lwc.hasAdminPermission(sender, "lwc.admin." + args[0].toLowerCase())) {
                event.setCancelled(true);
                return;
            }
        }

        return;
    }

}
