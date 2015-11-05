package org.grape.galaxy.game;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class GalaxyGames {

	private static Map<String, GalaxyGame> gamesMap = new LinkedHashMap<String, GalaxyGame>();

	public static GalaxyGame getGalaxyGame(String gameId) {
		return gamesMap.get(gameId);
	}

	public static Collection<GalaxyGame> getGalaxyGames() {
		return gamesMap.values();
	}

	static {
		GalaxyGame bowling = new GalaxyGame();
		bowling.setId("bowling");
		bowling.setName("Боулинг");
		bowling.setDescription("Реалистичный симулятор игры в боулинг с использованием"
				+ " популярного физического движка ODE для моделирования поведения"
				+ " игровых объектов (кеглей, шара и т.д.). В игре соблюдена спецификация"
				+ " Международной Ассоциации Боулинга по системе десяти кеглей"
				+ " (параметры кеглей, шара, дорожки, желоба и т.д.).");
		bowling.setKeywords("боулинг 3d, bowling 3d, боулинг ode, bowling ode,"
				+ " симулятор боулинга, bowling simulator, реалистичная игра боулинг,"
				+ " realistic bowling game, бесплатно боулинг 3d, free bowling 3d,"
				+ " онлайн боулинг 3d, online bowling 3d");
		bowling.setControls("Левая кнопка мыши или клавиша Space - выбор траектории"
				+ " и бросок шара.");
		gamesMap.put(bowling.getId(), bowling);

		GalaxyGame tetris = new GalaxyGame();
		tetris.setId("tetris");
		tetris.setName("Тетрис");
		tetris.setDescription("Сейчас уже мало кто помнит рождение тетриса - а"
				+ " произошло это в середине 80-х годов в СССР. В свое время"
				+ " это был настоящий компьютерный феномен, которым переболела"
				+ " сначала вся наша страна, а потом и весь мир. Многие"
				+ " авторитетные игровые издания по-прежнему считают тетрис"
				+ " лучшей игрой всех времен. Вашему вниманию представлена следующая"
				+ " модификация игры: возможность смены активной игровой доски во"
				+ " время игры.");
		tetris.setKeywords("тетрис 3d, tetris 3d, бесплатный тетрис 3d, free tetris 3d,"
				+ " онлайн тетрис 3d, online tetris 3d, бесплатно онлайн тетрис 3d,"
				+ " free online tetris 3d");
		tetris.setControls("Клавиши со стрелками - перемещение фигуры влево, вправо,"
				+ " поворот и бросок вниз. Клавиша Enter - переключение активной доски.");
		gamesMap.put(tetris.getId(), tetris);

		GalaxyGame lines = new GalaxyGame();
		lines.setId("lines");
		lines.setName("Линии");
		lines.setDescription("Распространенная и всеми любимая игра линии (она же"
				+ " шарики или лайнс). Игровое поле имеет размер 9x9 и состоит из"
				+ " ячеек, в которых лежат шарики. После каждого хода выставляются"
				+ " три шарика разных цветов. За каждый ход вы можете передвинуть"
				+ " только один шарик. Для этого вам нужно выделить его и указать"
				+ " для него новое местоположение. Чтобы шарик мог быть передвинут,"
				+ " необходимо, чтобы между клеткой местонахождения шарика и конечной"
				+ " клеткой его перемещения существовал путь из свободных клеток.");
		lines.setKeywords("линии 3d, лайнс 3d, шарики 3d, lines 3d, бесплатно линии 3d,"
				+ " бесплатно лайнс 3d, бесплатно шарики 3d, бесплатно lines 3d,"
				+ " free lines 3d, онлайн линии 3d, онлайн лайнс 3d, онлайн шарики 3d,"
				+ " онлайн lines 3d, online lines 3d");
		lines.setControls("Указатель мыши - наведение на зону поля или на шарик."
				+ " Левая кнопка мыши - выбор шарика для перемещения или"
				+ " перемещение выбранного шарика.");
		gamesMap.put(lines.getId(), lines);

		GalaxyGame magicballs = new GalaxyGame();
		magicballs.setId("magicballs");
		magicballs.setName("Магические Шары");
		magicballs
				.setDescription("Логическая игра, в которой нужно удалять с поля"
						+ " группы шариков одинакового цвета. Для прохождения уровня необходимо"
						+ " удалить с поля определенное количество шариков. При этом с заданным"
						+ " для каждого уровня интервалом времени на поле появляется новая партия"
						+ " разноцветных шариков.");
		magicballs
				.setKeywords("магические шары 3d, magic balls 3d,"
						+ " бесплатно магические шары 3d, free magic balls 3d,"
						+ " онлайн магические шары 3d, online magic balls 3d,"
						+ " бесплатно онлайн магические шары 3d, free online magic balls 3d");
		magicballs
				.setControls("Указатель мыши или клавиши со стрелками - перемещение"
						+ " 3D курсора влево, вправо, вверх и вниз. Левая кнопка мыши или"
						+ " клавиша Space - удаление соседних шаров одинакового цвета.");
		gamesMap.put(magicballs.getId(), magicballs);
	}
}
