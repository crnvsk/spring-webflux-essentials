package academy.devdojo.webflux.integration;

import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;

import academy.devdojo.webflux.domain.Anime;
import academy.devdojo.webflux.repository.AnimeRepository;
import academy.devdojo.webflux.util.AnimeCreator;
import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockingOperationError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@SpringBootTest
@AutoConfigureWebTestClient
public class AnimeControllerIT {

	private final static String REGULAR_USER = "carlos";
	private final static String ADMIN_USER = "joao";

	@MockitoBean
	private AnimeRepository animeRepositoryMock;

	@Autowired
	private WebTestClient client;

	private final Anime anime = AnimeCreator.createValidAnime();

	@BeforeAll
	public static void blockHoudSetup() {
		BlockHound.install(builder -> builder.allowBlockingCallsInside("java.util.UUID", "randomUUID"));
	}

	@BeforeEach
	public void setUp() {

		BDDMockito.when(animeRepositoryMock.findAll())
				.thenReturn(Flux.just(anime));

		BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
				.thenReturn(Mono.just(anime));

		BDDMockito.when(animeRepositoryMock.save(AnimeCreator.createAnimeToBeSaved()))
				.thenReturn(Mono.just(anime));

		BDDMockito.when(animeRepositoryMock
				.saveAll(List.of(AnimeCreator.createAnimeToBeSaved(),
						AnimeCreator.createAnimeToBeSaved())))
				.thenReturn(Flux.just(anime, anime));

		BDDMockito.when(animeRepositoryMock.delete(ArgumentMatchers.any(Anime.class)))
				.thenReturn(Mono.empty());

		BDDMockito.when(animeRepositoryMock.save(AnimeCreator.createValidAnime()))
				.thenReturn(Mono.empty());
	}

	@Test
	public void blockHoundWorks() {
		try {
			FutureTask<?> task = new FutureTask<>(() -> {
				Thread.sleep(0);
				return "";
			});
			Schedulers.parallel().schedule(task);

			task.get(10, TimeUnit.SECONDS);
			Assertions.fail("should fail");
		} catch (Exception e) {
			Assertions.assertTrue(e.getCause() instanceof BlockingOperationError);
		}
	}

	@Test
	@DisplayName("findAll returns a flux of anime when user is successfully authenticated and have the role ADMIN")
	@WithUserDetails(ADMIN_USER)
	public void findAll_ReturnsFluxOfAnimes_WhenSuccessful() {
		client
				.get()
				.uri("/animes")
				.exchange()
				.expectStatus().is2xxSuccessful()
				.expectBody()
				.jsonPath("$[0].id").isEqualTo(anime.getId())
				.jsonPath("$[0].name").isEqualTo(anime.getName());
	}

	// @Test
	// @DisplayName("listAll returns forbidden when user is successfully
	// authenticated and does not have role ADMIN")
	// @WithUserDetails(REGULAR_USER)
	// public void listAll_ReturnForbidden_WhenUserDoesNotHaveRoleAdmin() {
	// client
	// .get()
	// .uri("/animes")
	// .exchange()
	// .expectStatus().isForbidden();
	// }

	@Test
	@DisplayName("findAll returns unauthorized when user is not authenticated")
	public void findAll_ReturnsUnauthorized_WhenUserIsNotAuthenticated() {
		client
				.get()
				.uri("/animes")
				.exchange()
				.expectStatus().isUnauthorized();
	}

	@Test
	@DisplayName("findAll return a flux of animes when user is successfully authenticated and have the role ADMIN")
	@WithUserDetails(ADMIN_USER)
	public void findAll_Flavor2_ReturnFluxOfAnimes_WhenSuccessful() { // Forma nao recomendada em ambiente de producao
		client
				.get()
				.uri("/animes")
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(Anime.class)
				.hasSize(1)
				.contains(anime);
	}

	@Test
	@DisplayName("findById return a mono with anime when it exists and user is successfully authenticated and have the role USER")
	@WithUserDetails(REGULAR_USER)
	public void findById_ReturnMonoAnime_WhenSuccessful() {
		client
				.get()
				.uri("/animes/{id}", 1)
				.exchange()
				.expectStatus().isOk()
				.expectBody(Anime.class)
				.isEqualTo(anime);
	}

	@Test
	@DisplayName("findById return a mono error when anime does not exists and when user is successfully authenticated and have the role USER")
	@WithUserDetails(REGULAR_USER)
	public void findById_ReturnMonoError_WhenEmptyMonoIsReturned() {
		BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
				.thenReturn(Mono.empty());

		client
				.get()
				.uri("/animes/{id}", 1)
				.exchange()
				.expectStatus().isNotFound()
				.expectBody()
				.jsonPath("$.status").isEqualTo(404)
				.jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException was thrown");
	}

	@Test
	@DisplayName("save creates anime when successful and when user is successfully authenticated and have the role ADMIN")
	@WithUserDetails(ADMIN_USER)
	public void save_CreatesAnime_WhenSuccessful() {
		Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

		client
				.post()
				.uri("/animes")
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(animeToBeSaved))
				.exchange()
				.expectStatus().isCreated()
				.expectBody(Anime.class)
				.isEqualTo(anime);
	}

	@Test
	@DisplayName("saveBatch creates a list of anime when successful and when user is successfully authenticated and have the role ADMIN")
	@WithUserDetails(ADMIN_USER)
	public void saveBatch_CreatesListOfAnime_WhenSuccessful() {
		Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();

		client
				.post()
				.uri("/animes/batch")
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(List.of(animeToBeSaved, animeToBeSaved)))
				.exchange()
				.expectStatus().isCreated()
				.expectBodyList(Anime.class)
				.hasSize(2)
				.contains(anime);
	}

	@Test
	@DisplayName("save returns Mono error when anime is empty and when user is successfully authenticated and have the role ADMIN")
	@WithUserDetails(ADMIN_USER)
	public void save_ReturnsError_WhenNameIsEmpty() {
		Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved().withName("");

		client
				.post()
				.uri("/animes")
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(animeToBeSaved))
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.status").isEqualTo(400);
	}

	@Test
	@DisplayName("saveBatch returns Mono error when one of the objects in the list contains null or empty name and when user is successfully authenticated and have the role ADMIN")
	@WithUserDetails(ADMIN_USER)
	public void saveBatch_ReturnsMonoError_WhenContainsInvalidName() {
		Anime animeToBeSaved = AnimeCreator.createAnimeToBeSaved();
		Anime animeInvalid = AnimeCreator.createAnimeToBeSaved().withName("");

		client
				.post()
				.uri("/animes/batch")
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(List.of(animeToBeSaved, animeInvalid)))
				.exchange()
				.expectStatus().isBadRequest()
				.expectBody()
				.jsonPath("$.status").isEqualTo(400);
	}

	@Test
	@DisplayName("delete removes the anime when successful and when user is successfully authenticated and have the role ADMIN")
	@WithUserDetails(ADMIN_USER)
	public void delete_RemovesAnime_WhenSuccessful() {
		client
				.delete()
				.uri("/animes/{id}", 1)
				.exchange()
				.expectStatus().isNoContent();
	}

	@Test
	@DisplayName("delete return Mono error whe anime does not exist and when user is successfully authenticated and have the role ADMIN")
	@WithUserDetails(ADMIN_USER)
	public void delete_ReturnMonoError_WhenEmptyMonoIsReturned() {
		BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
				.thenReturn(Mono.empty());

		client
				.delete()
				.uri("/animes/{id}", 1)
				.exchange()
				.expectStatus().isNotFound()
				.expectBody()
				.jsonPath("$.status").isEqualTo(404)
				.jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException was thrown");
	}

	@Test
	@DisplayName("update save updated anime and returns empty Mono when successful and when user is successfully authenticated and have the role ADMIN")
	@WithUserDetails(ADMIN_USER)
	public void update_SaveUpdatedAnime_WhenSuccessful() {
		client
				.put()
				.uri("/animes/{id}", 1)
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(anime))
				.exchange()
				.expectStatus().isNoContent();
	}

	@Test
	@DisplayName("update return Mono error when anime does not exist and when user is successfully authenticated and have the role ADMIN")
	@WithUserDetails(ADMIN_USER)
	public void update_ReturnMonoError_WhenEmptyMonoIsReturned() {
		BDDMockito.when(animeRepositoryMock.findById(ArgumentMatchers.anyInt()))
				.thenReturn(Mono.empty());

		client
				.put()
				.uri("/animes/{id}", 1)
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(anime))
				.exchange()
				.expectStatus().isNotFound()
				.expectBody()
				.jsonPath("$.status").isEqualTo(404)
				.jsonPath("$.developerMessage").isEqualTo("A ResponseStatusException was thrown");
	}
}
