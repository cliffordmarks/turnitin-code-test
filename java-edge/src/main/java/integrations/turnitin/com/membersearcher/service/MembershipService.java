package integrations.turnitin.com.membersearcher.service;

import java.util.List;
import java.util.stream.Stream;
import java.util.concurrent.CompletableFuture;

import integrations.turnitin.com.membersearcher.client.MembershipBackendClient;
import integrations.turnitin.com.membersearcher.model.Membership;
import integrations.turnitin.com.membersearcher.model.MembershipList;
import integrations.turnitin.com.membersearcher.model.User;
import integrations.turnitin.com.membersearcher.model.UserList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MembershipService {
	@Autowired
	private MembershipBackendClient membershipBackendClient;


	/**
	 * This method simply just finds and returns all the users
	 *
	 * @return A CompletableFuture containing a UserList object.
	 */
	public CompletableFuture<UserList> fetchAllUsers() {
		return membershipBackendClient.fetchUsers();
	}

	/**
	 * Method to fetch all memberships with their associated user details included.
	 * This method calls out to the php-backend service and fetches all memberships,
	 * it then calls fetchUsers, as opposed to fetchUser(id), to fetch all users and
	 * associates them with their corresponding membership.
	 *
	 * @return A CompletableFuture containing a fully populated MembershipList object.
	 */
	public CompletableFuture<MembershipList> fetchAllMembershipsWithUsers() {
		return membershipBackendClient.fetchMemberships()
				.thenCompose(members -> {

					CompletableFuture<MembershipList> res = membershipBackendClient.fetchUsers()
							.thenCompose(userList -> {
								userList.getUsers().stream()
								.forEach(user -> setMemberShip(user, members.getMemberships()));

								return CompletableFuture.completedFuture(members);
							})
							.thenApply(vd -> members);
					return res;
					});

	}

	/**
	 * Sets the membership linked to the given user, with the user object.
	 */
	private void setMemberShip(User user, List<Membership>  memberships) {

		for (Membership member : memberships) {
			if (member.getUserId().equalsIgnoreCase(user.getId())) {
				member.setUser(user);
				break;
			}
		}
	}
}
