package sx.blah.discord.util;

import java.util.*;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.Permissions;

public class PermissionUtil {

	public static void checkUserHigher(IGuild guild, IUser user1, IUser user2) {
		if (!isUserHigher(guild, user1, user2)) throw new MissingPermissionsException("Attempt to interact with user of equal or higher position in role hierarchy.", null);
	}

	public static void checkUserHigher(IGuild guild, IUser user, List<IRole> roles) {
		if (!isUserHigher(guild, user, roles)) throw new MissingPermissionsException("Attempt to interact with user of equal or higher position in role hierarchy.", null);
	}

	public static boolean isUserHigher(IGuild guild, IUser user1, IUser user2) {
		if (guild.getOwner().equals(user1)) return true;
		if (guild.getOwner().equals(user2)) return false;

		return hasHigherRoles(guild.getRolesForUser(user1), guild.getRolesForUser(user2));
	}

	public static boolean isUserHigher(IGuild guild, IUser user, List<IRole> roles) {
		if (guild.getOwner().equals(user)) return true;
		return hasHigherRoles(guild.getRolesForUser(user), roles);
	}

	public static boolean hasHigherRoles(List<IRole> roles1, List<IRole> roles2) {
		OptionalInt maxPos1 = roles1.stream().mapToInt(IRole::getPosition).max();
		OptionalInt maxPos2 = roles2.stream().mapToInt(IRole::getPosition).max();

		return (maxPos1.isPresent() ? maxPos1.getAsInt() : 0) > (maxPos2.isPresent() ? maxPos2.getAsInt() : 0);
	}

	public static void checkPermissions(IGuild guild, IUser user, Permissions... required) {
		EnumSet<Permissions> temp = EnumSet.noneOf(Permissions.class);
		temp.addAll(Arrays.asList(required));
		checkPermissions(guild, user, temp);
	}

	public static void checkPermissions(IGuild guild, IUser user, EnumSet<Permissions> required) {
		required.removeAll(user.getPermissionsForGuild(guild));
		if (!required.isEmpty()) throw new MissingPermissionsException(required);
	}

	public static void checkPermissions(IChannel channel, IUser user, Permissions... required) {
		EnumSet<Permissions> temp = EnumSet.noneOf(Permissions.class);
		temp.addAll(Arrays.asList(required));
		checkPermissions(channel, user, temp);
	}

	public static void checkPermissions(IChannel channel, IUser user, EnumSet<Permissions> required) {
		required.removeAll(channel.getModifiedPermissions(user));
		if (!required.isEmpty()) throw new MissingPermissionsException(required);
	}

	public static void checkPermissionsAndHierarchy(IGuild guild, IUser user1, IUser user2, Permissions... permissions) {
		checkPermissions(guild, user1, permissions);
		checkUserHigher(guild, user1, user2);
	}

	public static void checkPermissionsAndHierarchy(IGuild guild, IUser user, List<IRole> roles, Permissions... permissions) {
		checkPermissions(guild, user, permissions);
		checkUserHigher(guild, user, roles);
	}

	public static void checkPermissionsAndHierarchy(IChannel channel, IUser user1, IUser user2, Permissions... permissions) {
		checkPermissions(channel, user1, permissions);
		checkUserHigher(channel.getGuild(), user1, user2);
	}

	public static void checkPermissionsAndHierarchy(IChannel channel, IUser user, List<IRole> roles, Permissions... permissions) {
		checkPermissions(channel, user, permissions);
		checkUserHigher(channel.getGuild(), user, roles);
	}
}
