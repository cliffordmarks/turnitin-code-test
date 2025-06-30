package integrations.turnitin.com.membersearcher;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.fasterxml.jackson.databind.ObjectMapper;
import integrations.turnitin.com.membersearcher.client.MembershipBackendClient;
import integrations.turnitin.com.membersearcher.model.Membership;
import integrations.turnitin.com.membersearcher.model.MembershipList;
import integrations.turnitin.com.membersearcher.model.User;
import integrations.turnitin.com.membersearcher.model.UserList;
import integrations.turnitin.com.membersearcher.service.MembershipService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MembershipServiceTest {
	@InjectMocks
	private MembershipService membershipService;

	@Mock
	private MembershipBackendClient membershipBackendClient;

	@Mock
	private ObjectMapper objectMapper;

	private MembershipList members;

	private UserList userList;

	private User userOne;

	private User userTwo;

	@BeforeEach
	public void init() {
		members = new MembershipList()
				.setMemberships(List.of(
						new Membership()
								.setId("a")
								.setRole("instructor")
								.setUserId("1"),
						new Membership()
								.setId("b")
								.setRole("student")
								.setUserId("2")
				));
		userOne = new User()
				.setId("1")
				.setName("test one")
				.setEmail("test1@example.com");
		userTwo = new User()
				.setId("2")
				.setName("test two")
				.setEmail("test2@example.com");
		
		userList = new UserList()
				.setUsers(List.of(userOne, userTwo));

	}


	/**
	 * Common code used by more than one tests.
	 */
	private void commonInitSetup() {
		
		when(membershipBackendClient.fetchUsers()).thenReturn(CompletableFuture.completedFuture(userList));
	}
	
	
	/**
	 * Add when test specific to the TestFetchAllMemberships test - avoids mockito unnecessary stub exceptions
	 */
	private void initTestFetchAllMemberships() {
		
		when(membershipBackendClient.fetchMemberships()).thenReturn(CompletableFuture.completedFuture(members));
		commonInitSetup();
	}
	
	/**
	 * Add when test specific to the TestFetchAllUsers test - avoids mockito unnecessary stub exceptions
	 */
	private void initTestFetchAllUsers() {
		commonInitSetup();
	}
	
	/**
	 * Added fetchAllMembershipsWithUsers to test that service call to get memberships and then all users
	 * in one go, as opposed to individually via fetchUser(id) calls, returns expected memberships populated
	 * with expected user data.  
	 * @throws Exception
	 */
	@Test
	public void TestFetchAllMemberships() throws Exception {
		
		initTestFetchAllMemberships();
		
		MembershipList members = membershipService.fetchAllMembershipsWithUsers().get();
		assertThat(members.getMemberships().get(0).getUser()).isEqualTo(userOne);
		assertThat(members.getMemberships().get(1).getUser()).isEqualTo(userTwo);
	}

	/**
	 * Added a basic fetchAllUsers test to ensure we get test users. 
	 * @throws Exception
	 */
	@Test
	public void TestFetchAllUsers() throws Exception {
		
		initTestFetchAllUsers();
		
		UserList users = membershipService.fetchAllUsers().get();
		assertNotNull(users.getUsers());
		assertFalse(users.getUsers().isEmpty());
	}
}
