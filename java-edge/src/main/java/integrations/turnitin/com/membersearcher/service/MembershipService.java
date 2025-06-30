package integrations.turnitin.com.membersearcher.service;

import java.util.concurrent.CompletableFuture;

import integrations.turnitin.com.membersearcher.client.MembershipBackendClient;
import integrations.turnitin.com.membersearcher.model.MembershipList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MembershipService {
	@Autowired
	private MembershipBackendClient membershipBackendClient;

	/**
	 * Method to fetch all memberships with their associated user details included.
	 * This method calls out to the php-backend service and fetches all memberships,
	 * it then calls to fetch the user details for each user individually and
	 * associates them with their corresponding membership.
	 *
	 * @return A CompletableFuture containing a fully populated MembershipList object.
	 */
	public CompletableFuture<MembershipList> fetchAllMembershipsWithUsers() {
		return membershipBackendClient.fetchMemberships()
				.thenCompose(members -> {
					CompletableFuture<?>[] userCalls = members.getMemberships().stream()
							.map(member -> membershipBackendClient.fetchUser(member.getUserId())
									.thenApply(member::setUser))
							.toArray(CompletableFuture<?>[]::new);
					return CompletableFuture.allOf(userCalls)
							.thenApply(nil -> members);
				});
	}

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
	 * it then calls to fetch the user details for each user individually and
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

}
