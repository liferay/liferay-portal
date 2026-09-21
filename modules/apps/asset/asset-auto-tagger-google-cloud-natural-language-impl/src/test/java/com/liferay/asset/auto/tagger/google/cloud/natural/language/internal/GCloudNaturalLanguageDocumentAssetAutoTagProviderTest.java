/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.auto.tagger.google.cloud.natural.language.internal;

import com.liferay.asset.auto.tagger.google.cloud.natural.language.internal.configuration.GCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author Cristina González
 */
public class GCloudNaturalLanguageDocumentAssetAutoTagProviderTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		_gCloudNaturalLanguageDocumentAssetAutoTagProvider =
			new GCloudNaturalLanguageDocumentAssetAutoTagProvider();

		ReflectionTestUtil.setFieldValue(
			_gCloudNaturalLanguageDocumentAssetAutoTagProvider, "_jsonFactory",
			new JSONFactoryImpl());
	}

	@Test
	public void testGetClassificationTagNamesWithDisabledClassifcationEndpoint() {
		GCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration
			gCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration =
				new GCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration() {

					@Override
					public String apiKey() {
						return null;
					}

					@Override
					public boolean classificationEndpointEnabled() {
						return false;
					}

					@Override
					public float confidence() {
						return 0;
					}

					@Override
					public String[] enabledClassNames() {
						return new String[0];
					}

					@Override
					public boolean entityEndpointEnabled() {
						return false;
					}

					@Override
					public float salience() {
						return 0;
					}

				};

		Set<String> tagNames = ReflectionTestUtil.invoke(
			_gCloudNaturalLanguageDocumentAssetAutoTagProvider,
			"_getClassificationTagNames",
			new Class<?>[] {
				long.class,
				GCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration.class,
				String.class, Locale.class
			},
			RandomTestUtil.randomLong(),
			gCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration,
			RandomTestUtil.randomString(), null);

		Assert.assertEquals(
			tagNames.toString(), Collections.emptySet(), tagNames);
	}

	@Test
	public void testGetClassificationTagNamesWithInvalidLanguage() {
		GCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration
			gCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration =
				new GCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration() {

					@Override
					public String apiKey() {
						return null;
					}

					@Override
					public boolean classificationEndpointEnabled() {
						return true;
					}

					@Override
					public float confidence() {
						return 0;
					}

					@Override
					public String[] enabledClassNames() {
						return new String[0];
					}

					@Override
					public boolean entityEndpointEnabled() {
						return true;
					}

					@Override
					public float salience() {
						return 0;
					}

				};

		Set<String> tagNames = ReflectionTestUtil.invoke(
			_gCloudNaturalLanguageDocumentAssetAutoTagProvider,
			"_getClassificationTagNames",
			new Class<?>[] {
				long.class,
				GCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration.class,
				String.class, Locale.class
			},
			RandomTestUtil.randomLong(),
			gCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration,
			RandomTestUtil.randomString(), LocaleUtil.GERMAN);

		Assert.assertEquals(
			tagNames.toString(), Collections.emptySet(), tagNames);
	}

	@Test
	public void testGetEntitiesTagNamesWithDisabledEntitiesEndpoint() {
		GCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration
			gCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration =
				new GCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration() {

					@Override
					public String apiKey() {
						return null;
					}

					@Override
					public boolean classificationEndpointEnabled() {
						return false;
					}

					@Override
					public float confidence() {
						return 0;
					}

					@Override
					public String[] enabledClassNames() {
						return new String[0];
					}

					@Override
					public boolean entityEndpointEnabled() {
						return false;
					}

					@Override
					public float salience() {
						return 0;
					}

				};

		Set<String> tagNames = ReflectionTestUtil.invoke(
			_gCloudNaturalLanguageDocumentAssetAutoTagProvider,
			"_getEntitiesTagNames",
			new Class<?>[] {
				long.class,
				GCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration.class,
				String.class, Locale.class
			},
			RandomTestUtil.randomLong(),
			gCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration,
			RandomTestUtil.randomString(), null);

		Assert.assertEquals(
			tagNames.toString(), Collections.emptySet(), tagNames);
	}

	@Test
	public void testGetEntitiesTagNamesWithInvalidLanguage() {
		GCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration
			gCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration =
				new GCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration() {

					@Override
					public String apiKey() {
						return null;
					}

					@Override
					public boolean classificationEndpointEnabled() {
						return false;
					}

					@Override
					public float confidence() {
						return 0;
					}

					@Override
					public String[] enabledClassNames() {
						return new String[0];
					}

					@Override
					public boolean entityEndpointEnabled() {
						return false;
					}

					@Override
					public float salience() {
						return 0;
					}

				};

		Set<String> tagNames = ReflectionTestUtil.invoke(
			_gCloudNaturalLanguageDocumentAssetAutoTagProvider,
			"_getEntitiesTagNames",
			new Class<?>[] {
				long.class,
				GCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration.class,
				String.class, Locale.class
			},
			RandomTestUtil.randomLong(),
			gCloudNaturalLanguageAssetAutoTaggerCompanyConfiguration,
			RandomTestUtil.randomString(), LocaleUtil.TAIWAN);

		Assert.assertEquals(
			tagNames.toString(), Collections.emptySet(), tagNames);
	}

	@Test
	public void testToTagNamesWithClassification() throws Exception {
		JSONObject responseJSONObject = JSONFactoryUtil.createJSONObject(
			new String(
				FileUtil.getBytes(
					getClass(), "dependencies/response_classification.json")));

		Set<String> expectedTagNames = new HashSet<>(
			Arrays.asList(
				"Literature", "Teens", "Childrens Literature", "People",
				"Society", "Books", "Kids", "Childrens Interests"));

		Set<String> actualTagNames = ReflectionTestUtil.invoke(
			_gCloudNaturalLanguageDocumentAssetAutoTagProvider, "_toTagNames",
			new Class<?>[] {JSONArray.class, Predicate.class},
			responseJSONObject.getJSONArray("categories"),
			(Predicate<JSONObject>)jsonObject -> true);

		Assert.assertTrue(
			actualTagNames.toString(),
			actualTagNames.containsAll(expectedTagNames));
	}

	@Test
	public void testToTagNamesWithEntities() throws Exception {
		JSONObject responseJSONObject = JSONFactoryUtil.createJSONObject(
			new String(
				FileUtil.getBytes(
					getClass(), "dependencies/response_entities.json")));

		Set<String> expectedTagNames = new HashSet<>(
			Arrays.asList(
				"pope", "frowning", "hands", "half", "egg", "thimble",
				"CHAPTER I.", "hall", "EVERYBODY", "Repeat", "explanation",
				"branch", "youth", "kettle", "tea", "twinkling", "Everything",
				"music", "rabbit", "legs", "schoolroom", "silence", "sorts",
				"sister", "Mary Ann", "chimneys", "YOU.", "pool", "hatters",
				"CHAPTER II", "shaking", "eat bats", "planning", "charges",
				"size", "stop", "temper", "Bill", "wink", "Author", "much",
				"wine", "wind", "C", "D", "airs", "throat", "WATCH",
				"Ou est ma chatte", "M", "sleep", "result", "same", "French",
				"variations", "Mine", "mousetraps", "hand", "flavour",
				"Shakespeare", "steamengine", "Nobody", "neck", "teaparty",
				"lips", "garden door", "Ada", "Pat", "couple", "pigs",
				"cupboards", "ferrets", "cakes", "serpent", "Little Bill",
				"conversation", "others", "murder", "invitation", "paw",
				"respect", "sand", "toast", "skyrocket", "authority", "Footman",
				"creature", "child", "Hole", "chimney", "Canterbury", "harm",
				"Maam", "rathole", "rope", "sneezing", "end", "mistake", "roof",
				"wits", "label", "room", "Project Gutenberg License",
				"contempt", "truth", "Mouse", "well", "family", "age", "pocket",
				"shoulders", "FishFootman", "Nay", "fishes", "number",
				"disappointment", "bathing machines", "judge", "darkness",
				"pet", "order", "eggs", "shape", "nothing", "waterwell", "IT",
				"Some", "green stuff", "arrum", "Jackinthebox", "despair",
				"eBook", "queer", "other", "woman", "tone", "White Rabbit",
				"air", "matter", "footsteps", "courage", "caterpillar", "top",
				"Elsie", "noise", "violence", "question", "raven", "housemaid",
				"brown hair", "Pig", "cherrytart", "hippopotamus", "rosetree",
				"names", "Pool of Tears", "spades", "teatray", "capital",
				"shake", "tops", "manner", "trial", "pig", "carthorse", "pegs",
				"lap", "snout", "entrance", "all", "pigeon", "terror",
				"circumstances", "law", "remedies", "yelp", "pardon", "plenty",
				"clock", "Ma", "WAISTCOATPOCKET", "arms", "rudeness", "Why?",
				"Dinah", "cause", "grunt", "treaclewell", "times", "whispers",
				"gardeners", "milkjug", "row", "floor", "direction", "Mercia",
				"any", "opening", "English", "ladder", "pineapple", "conduct",
				"whisper", "walk", "reason", "thought", "ring", "rabbits",
				"concert", "pity", "cake", "Alices Adventures in Wonderland",
				"jar", "state", "shoulder", "meeting", "jaw",
				"Project Gutenberg", "Lewis Carroll", "voices", "eye", "letter",
				"Latin Grammar", "telescope", "circle", "Mabel", "cup", "cur",
				"ONE", "another", "thistle", "two", "prize", "notion", "Heads",
				"March Hare", "patience", "CHAPTER VI", "kitchen", "arm",
				"escape", "jaws", "poison", "nest", "curls", "kick", "watch",
				"speech", "Sh", "livery", "Has", "thing", "feelings",
				"cartwheels", "Normans", "strength", "CHAPTER VIII", "Anything",
				"daisychain", "mouse", "fellows", "feathers", "blows", "things",
				"dogs", "glass", "cucumberframe", "suet", "doubt", "girl",
				"BUSY BEE", "difficulty", "Caucusrace", "name", "swim",
				"pepper", "crocodile", "Christmas", "memory", "distance", "Yes",
				"book", "snatch", "magic bottle", "house", "mice", "anything",
				"conclusion", "barrowful", "gloves", "currants", "brass plate",
				"undoing", "earls", "severity", "racecourse", "toys", "race",
				"shore", "chrysalis", "WHAT", "way", "pair", "sensation",
				"mouth", "Five", "CroquetGround", "EAT ME", "cauldron",
				"window", "time", "flame", "play", "fancy", "commotion",
				"head over heels", "three", "Lory", "fan", "lessonbook", "fat",
				"fountains", "morsel", "plan", "case", "The Rabbit",
				"Edgar Atheling", "waistcoatpocket", "flowers", "lesson",
				"LITTLE", "Seven", "fryingpan", "coast", "sticks", "moon",
				"telescopes", "prizes", "consultation", "care", "denial",
				"glass box", "hookah", "fireplace", "Morcar", "ways", "cushion",
				"growl", "manners", "salt water", "coaxing", "grin",
				"rabbithole", "more", "Canary", "Multiplication Table",
				"centre", "dishes", "usurpation", "cats", "ravens", "fellow",
				"sides", "stockings", "garden", "heads", "stuff", "door",
				"shut", "saucepans", "beak", "MILLENNIUM FULCRUM EDITION 3.0",
				"paper label", "bookshelves", "Twinkle", "Magpie", "whiles",
				"drinking", "curiosity", "kind", "Queen", "tree", "history",
				"water", "pause", "New Zealand", "most", "whiskers", "CAN",
				"bones", "effect", "apples", "daughter", "game", "riddles.",
				"remark", "conversations", "sounds", "writingdesks",
				"catch hold", "CHORUS", "CHAPTER IV", "rest", "ceiling",
				"butter", "teapot", "terrier", "oyster", "ears", "neither",
				"front", "hurry", "Pepper", "fig", "beasts", "bat", "rumbling",
				"custard", "railway", "buttercup", "knowledge", "banks", "milk",
				"opportunity", "Queen of Hearts", "puppy", "SOMETHING",
				"tea spoon", "PLENTY", "Prizes", "beds", "brother",
				"Northumbria", "deal", "Long Tale", "shiver", "bottle", "eyes",
				"sea", "claws", "adoption", "Eaglet", "HEARTHRUG", "grass",
				"hearing", "dear", "foot", "mind", "chin", "business", "tail",
				"words", "lodging houses", "shoes", "writingdesk", "answer",
				"series", "MINE", "CURTSEYING", "bed", "stairs", "questions",
				"promise", "girls", "hold", "tale", "Rome", "lookout",
				"Stigand", "bird", "reply", "Talking", "Shy", "teathings",
				"Nile", "knife", "acceptance", "piece", "blame", "paws",
				"burst", "Sir", "confusion", "kid gloves", "party", "little",
				"some", "limbs", "tears", "waiting", "beginning", "SOMEWHERE",
				"goose", "pattering", "SOMEBODY", "back", "apiece", "riddle",
				"nightair", "Crab", "rate", "trouble", "doze", "rats", "sir",
				"notice", "jury", "living", "length", "snappishly", "dinner",
				"home", "Treacle", "walrus", "baby", "farmer", "Duck", "tunnel",
				"presents", "practice", "crowd", "Lizard", "answers", "bit",
				"attempt", "muchness", "agony", "corner", "stalk", "bend",
				"lock", "animals", "letters", "fear", "crown", "sentence",
				"song", "sky", "tiptoe", "plates", "ointment", "serpents",
				"sense", "doorway", "somebody", "bough", "Cheshire", "field",
				"messages", "dog", "each other", "works", "FENDER", "Edwin",
				"Rabbit", "grey locks", "plate", "world", "footman", "nurse",
				"man", "everything", "table", "delight", "side", "ESQ",
				"change", "comfort", "candle", "fun", "feet", "sigh", "fur",
				"instance", "use", "glass table", "subject", "sob", "anger",
				"smile", "edge", "high", "son", "Alice", "poker",
				"restrictions", "sort", "feeling", "roots", "friends",
				"ringlets", "directions", "dish", "position", "curtain",
				"CHAPTER V. Advice", "Cheshire Puss", "soup", "fairytales",
				"best", "box", "boy", "nonsense", "head", "footmen", "breath",
				"_I_", "Dinah!", "ALICE", "hedges", "backsomersault", "heap",
				"starfish", "chance", "lullaby", "cross", "Caterpillar",
				"master", "fright", "puzzle", "earth", "howling", "Paris",
				"remarks", "Antipathies", "stool", "blades", "elbow", "paint",
				"somewhere", "trees", "crash", "bank", "sizes", "meaning",
				"drawing", "fire", "stick", "mushroom", "height", "proposal",
				"pleasure", "Longitude", "something", "zigzag", "roast turkey",
				"London", "animal", "lessons", "shower", "fact", "One",
				"O mouse", "pieces", "croquet", "Dodo", "butterfly", "boots",
				"The Duchess", "insolence", "key", "honour", "daisies",
				"twinkle", "middle", "toffee", "no one", "one", "many",
				"people", "face", "fish", "flowerbeds", "fourth", "Australia",
				"story", "hand in hand", "country", "rules", "Latitude",
				"locks", "pairs", "Sounds", "leaves", "guineapigs",
				"William the Conqueror", "bark", "wood", "Duchess", "passion",
				"riddles", "waters", "surprise", "maps", "CHAPTER III",
				"conquest", "Pigeon", "look", "archbishop", "crumbs", "failure",
				"hint", "cucumberframes", "knot", "sighing", "saucer",
				"reasons", "figures", "idea", "father", "changes", "breadknife",
				"railway station", "Time", "cold", "life", "speed", "shelves",
				"bats", "interest", "wonder", "shriek", "Mad TeaParty",
				"Brandy", "Title", "histories", "ones", "FrogFootman",
				"disgust", "reach", "axes", "Bill!", "lessonbooks", "none",
				"hiss", "children", "terms", "sight", "teeth", "nobody",
				"slate", "CaucusRace", "young lady", "comfits", "worm",
				"overhead", "wife", "shilling", "doors", "anyone", "Next",
				"word", "tongue", "forehead", "fireirons", "birds", "pictures",
				"LOVE", "Cheshire Cat", "fall", "Lacie", "Fury", "ear",
				"course", "leap", "place", "finger", "cook", "elbows",
				"O Mouse", "brain", "roof bear", "opinion", "help", "seaside",
				"wings", "chorus", "I DONT", "Goodbye", "first", "roses",
				"voice", "Geography", "argument", "passage", "lamps", "sound",
				"existence", "leaders", "axis", "frog", "spite", "armchair",
				"sage", "verse", "blow", "CHAPTER VII", "pebbles", "creatures",
				"resource", "bottom", "ORANGE MARMALADE", "Hatter", "branches",
				"sulky", "UTF8", "person", "Cat", "ground", "W. RABBIT",
				"Dormouse", "Serpent", "everybody", "death", "advice", "line",
				"scale", "paw round", "hedge", "eel", "hair", "pretexts", "cat",
				"hearth", "knuckles", "alarm", "saucepan", "FATHER WILLIAM",
				"treacle", "listening", "nose", "Tillie", "cost", "advantage",
				"smoke", "sisters", "chimney close", "rush", "dears", "carrier",
				"round"));

		Set<String> actualTagNames = ReflectionTestUtil.invoke(
			_gCloudNaturalLanguageDocumentAssetAutoTagProvider, "_toTagNames",
			new Class<?>[] {JSONArray.class, Predicate.class},
			responseJSONObject.getJSONArray("entities"),
			(Predicate<JSONObject>)jsonObject -> true);

		Assert.assertTrue(
			actualTagNames.toString(),
			actualTagNames.containsAll(expectedTagNames));
	}

	private static GCloudNaturalLanguageDocumentAssetAutoTagProvider
		_gCloudNaturalLanguageDocumentAssetAutoTagProvider;

}