package sx.blah.discord.util;

import sx.blah.discord.handle.obj.*;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.OptionalInt;

public class PermissionUtils {

	public static void requireUserHigher(IGuild guild, IUser user1, IUser user2) {
		if (!isUserHigher(guild, user1, user2))
			throw new MissingPermissionsException("Attempt to interact with user of equal or higher position in role hierarchy.", null);
	}

	public static void requireUserHigher(IGuild guild, IUser user, List<IRole> roles) {
		if (!isUserHigher(guild, user, roles))
			throw new MissingPermissionsException("Attempt to interact with user of equal or higher position in role hierarchy.", null);
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

	public static void requirePermissions(IGuild guild, IUser user, Permissions... required) {
		requirePermissions(guild, user, arrayToEnumSet(required));
	}

	public static void requirePermissions(IGuild guild, IUser user, EnumSet<Permissions> required) {
		EnumSet<Permissions> copy = required.clone();
		copy.removeAll(user.getPermissionsForGuild(guild));
		if (!copy.isEmpty()) throw new MissingPermissionsException(copy);
	}

	public static void requirePermissions(IChannel channel, IUser user, Permissions... required) {
		requirePermissions(channel, user, arrayToEnumSet(required));
	}

	public static void requirePermissions(IChannel channel, IUser user, EnumSet<Permissions> required) {
		EnumSet<Permissions> copy = required.clone();
		copy.removeAll(channel.getModifiedPermissions(user));
		if (!copy.isEmpty()) throw new MissingPermissionsException(copy);
	}

	public static boolean hasPermissions(IGuild guild, IUser user, Permissions... required) {
		return hasPermissions(guild, user, arrayToEnumSet(required));
	}

	public static boolean hasPermissions(IGuild guild, IUser user, EnumSet<Permissions> required) {
		EnumSet<Permissions> copy = required.clone();
		copy.removeAll(user.getPermissionsForGuild(guild));
		return copy.isEmpty();
	}

	public static boolean hasPermissions(IChannel channel, IUser user, Permissions... required) {
		return hasPermissions(channel, user, arrayToEnumSet(required));
	}

	public static boolean hasPermissions(IChannel channel, IUser user, EnumSet<Permissions> required) {
		EnumSet<Permissions> copy = required.clone();
		copy.removeAll(channel.getModifiedPermissions(user));
		return copy.isEmpty();
	}

	public static void requireHierarchicalPermissions(IGuild guild, IUser user1, IUser user2, Permissions... permissions) {
		requireHierarchicalPermissions(guild, user1, user2, arrayToEnumSet(permissions));
	}

	public static void requireHierarchicalPermissions(IGuild guild, IUser user1, IUser user2, EnumSet<Permissions> permissions) {
		requirePermissions(guild, user1, permissions);
		requireUserHigher(guild, user1, user2);
	}

	public static void requireHierarchicalPermissions(IGuild guild, IUser user, List<IRole> roles, Permissions... permissions) {
		requireHierarchicalPermissions(guild, user, roles, arrayToEnumSet(permissions));
	}

	public static void requireHierarchicalPermissions(IGuild guild, IUser user, List<IRole> roles, EnumSet<Permissions> permissions) {
		requirePermissions(guild, user, permissions);
		requireUserHigher(guild, user, roles);
	}

	public static void requireHierarchicalPermissions(IChannel channel, IUser user1, IUser user2, Permissions... permissions) {
		requireHierarchicalPermissions(channel, user1, user2, arrayToEnumSet(permissions));
	}

	public static void requireHierarchicalPermissions(IChannel channel, IUser user1, IUser user2, EnumSet<Permissions> permissions) {
		requirePermissions(channel, user1, permissions);
		requireUserHigher(channel.getGuild(), user1, user2);
	}

	public static void requireHierarchicalPermissions(IChannel channel, IUser user, List<IRole> roles, Permissions... permissions) {
		requireHierarchicalPermissions(channel, user, roles, arrayToEnumSet(permissions));
	}

	public static void requireHierarchicalPermissions(IChannel channel, IUser user, List<IRole> roles, EnumSet<Permissions> permissions) {
		requirePermissions(channel, user, permissions);
		requireUserHigher(channel.getGuild(), user, roles);
	}

	public static boolean hasHierarchicalPermissions(IGuild guild, IUser user1, IUser user2, Permissions... permissions) {
		return hasHierarchicalPermissions(guild, user1, user2, arrayToEnumSet(permissions));
	}

	public static boolean hasHierarchicalPermissions(IGuild guild, IUser user1, IUser user2, EnumSet<Permissions> permissions) {
		return hasPermissions(guild, user1, permissions) && isUserHigher(guild, user1, user2);
	}

	public static boolean hasHierarchicalPermissions(IGuild guild, IUser user, List<IRole> roles, Permissions... permissions) {
		return hasHierarchicalPermissions(guild, user, roles, arrayToEnumSet(permissions));
	}

	public static boolean hasHierarchicalPermissions(IGuild guild, IUser user, List<IRole> roles, EnumSet<Permissions> permissions) {
		return hasPermissions(guild, user, permissions) && isUserHigher(guild, user, roles);
	}

	public static boolean hasHierarchicalPermissions(IChannel channel, IUser user1, IUser user2, Permissions... permissions) {
		return hasHierarchicalPermissions(channel, user1, user2, arrayToEnumSet(permissions));
	}

	public static boolean hasHierarchicalPermissions(IChannel channel, IUser user1, IUser user2, EnumSet<Permissions> permissions) {
		return hasPermissions(channel, user1, permissions) && isUserHigher(channel.getGuild(), user1, user2);
	}

	public static boolean hasHierarchicalPermissions(IChannel channel, IUser user, List<IRole> roles, Permissions... permissions) {
		return hasHierarchicalPermissions(channel, user, roles, arrayToEnumSet(permissions));
	}

	public static boolean hasHierarchicalPermissions(IChannel channel, IUser user, List<IRole> roles, EnumSet<Permissions> permissions) {
		return hasPermissions(channel, user, permissions) && isUserHigher(channel.getGuild(), user, roles);
	}

	private static boolean hasHigherRoles(List<IRole> roles1, List<IRole> roles2) {
		OptionalInt maxPos1 = roles1.stream().mapToInt(IRole::getPosition).max();
		OptionalInt maxPos2 = roles2.stream().mapToInt(IRole::getPosition).max();

		return (maxPos1.isPresent() ? maxPos1.getAsInt() : 0) > (maxPos2.isPresent() ? maxPos2.getAsInt() : 0);
	}

	private static EnumSet<Permissions> arrayToEnumSet(Permissions... array) {
		EnumSet<Permissions> set = EnumSet.noneOf(Permissions.class);
		set.addAll(Arrays.asList(array));
		return set;
	}
}
