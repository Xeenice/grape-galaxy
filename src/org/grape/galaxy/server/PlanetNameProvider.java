package org.grape.galaxy.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanetNameProvider {

	private static PlanetNameProvider instance;
	private String planetNames[] = new String[] { "Альферац", "Сиррах",
			"Мирах", "Аламак", "Альмак", "Кастор", "Поллукс", "Алхена",
			"Васат", "Мебсута", "Мехбуда", "Дирах", "Теят", "Постериор",
			"Дубхе", "Мерак", "Фекда", "Мегрец", "Каффа", "Алиот", "Мицар",
			"Алькор", "Бенетнаш", "Алкаид", "Талитха", "Бореалис", "Аустралис",
			"Сириус", "Каникула", "Мирзам", "Мулифен", "Мулифан", "Везен",
			"Фуруд", "Исида", "Киффа", "Зубенэльгенуби", "Зубенэшемали",
			"Зубенэльакраб", "Зубенэльакриби", "Зубенхакраби", "Садалмелик",
			"Садалсууд", "Садахбия", "Скат", "Шеат", "Албали", "Анха",
			"Капелла", "Альхайот", "Менкалинан", "Арктур", "Неккар", "Мерез",
			"Сегинус", "Харрис", "Изар", "Мирак", "Пульхерима", "Мюфрид",
			"Мифрид", "Алкалуропс", "Диадема", "Алхиба", "Дженах", "Гиенах",
			"Алгораб", "Минкар", "Алгети", "Корнефорос", "Рутилик", "Марсик",
			"Марфак", "Маасим", "Асфард", "Альфард", "Факт", "Везн", "Кор",
			"Астерион", "Хара", "Спика", "Азимех", "Дана", "Завиява",
			"Завиджава", "Алараф", "Поррима", "Виндемиатрикс", "Заниах",
			"Заннах", "Сирма", "Суалокин", "Ротанев", "Денеб", "Тубан",
			"Растабан", "Альваид", "Надус", "Гиансар", "Арракис", "Аррафид",
			"Алнаир", "Арнеб", "Нихал", "Альхаг", "Себальраи", "Цельбайрай",
			"Йед", "Сабик", "Альсабик", "Марфик", "Синистра", "Унакалхаи",
			"Алия", "Шедар", "Шедир", "Каф", "Шаф", "Рухбах", "Рукбах",
			"Канопус", "Сухейль", "Форамен", "Туренс", "Турайс", "Менкар",
			"Мекаб", "Кайтос", "Дифда", "Каффалджидхма", "Кантос", "Алгенуби",
			"Мира", "Гиеди", "Альгеди", "Альджери", "Дихабда", "Дабих",
			"Нашира", "Алгеди", "Шедди", "Кастра", "Арм", "Окул", "Наос",
			"Маркеб", "Аридиф", "Альбирео", "Садр", "Азельфафага", "Регул",
			"Кальб", "Денебола", "Алгиеба", "Альгейба", "Альджеба", "Зосма",
			"Альгенуби", "Адхафера", "Альджабхах", "Кокса", "Хорт", "Алтерф",
			"Расалес", "Вега", "Шелиак", "Сулафат", "Киносура", "Альрукаба",
			"Кохаб", "Феркад", "Илдун", "Китальфа", "Процион", "Гомайза",
			"Гомейза", "Хамаль", "Шератан", "Месарсим", "Месартим", "Ботеин",
			"Альтаир", "Атаир", "Альшаин", "Таразед", "Денебокаб",
			"Бетельгейзе", "Ригель", "Альгебар", "Беллактрис", "Минтака",
			"Алнилам", "Алнитак", "Сайф", "Альхека", "Хека", "Мейсса",
			"Алсулхаил", "Регор", "Маркаб", "Альгениб", "Эниф", "Хомам",
			"Матар", "Мирфак", "Алголь", "Мисам", "Менкиб", "Акубенс", "Тарф",
			"Азеллюс", "Алриша", "Реша", "Каитайн", "Окда", "Альфарг", "Гемма",
			"Альфекка", "Гнозия", "Нусакан", "Антарес", "Веспертилио", "Акраб",
			"Эльакраб", "Джубба", "Граффиас", "Шаула", "Лесат", "Лезат",
			"Лезах", "Джабхат", "Рукбат", "Альрами", "Нушаба", "Меридианалис",
			"Аскела", "Асцелла", "Полис", "Альталимайн", "Манубрий", "Нунки",
			"Пелаг", "Теребел", "Альдебаран", "Палилия", "Натх", "Аин",
			"Альциона", "Целано", "Электра", "Тайгета", "Майя", "Астеропа",
			"Меропа", "Атлас", "Плейона", "Металлах", "Толиман", "Агена",
			"Хадар", "Альдерамин", "Альфирк", "Альрай", "Эррай", "Алкес",
			"Лабр", "Ахернар", "Курса", "Зурак", "Акамар", "Бейд", "Акрукс",
			"Бекрукс", "Гакрукс", "Фомальгаут", "Атриа", "Пропус", "Алюрда",
			"Ситула", "Хедус", "Хассалех", "Мерга", "Краз", "Сарин", "Каям",
			"Авва", "Хезе", "Тиль", "Кума", "Грумиум", "Хан", "Ких", "Ахирд",
			"Миаплацидус", "Авиор", "Асмидиске", "Субра", "Хатиса", "Табит",
			"Сальма", "Мирам", "Атик", "Презепа", "Лесатх", "Джаббах",
			"Алният", "Альбальдах", "Анкаа", "Турайс", "Эракис", "Рана",
			"Зибаль", "Азха", "Тееним", "Скип" };
	private String greekDigits[] = new String[] { "I", "II", "III", "IV", "V",
			"VI", "VII", "VIII", "IX", "X", "XI", "XII", "XIII", "XIV", "XV",
			"XVI", "XVII", "XVIII", "XIX", "XX" };

	private PlanetNameProvider() {
	}

	public static PlanetNameProvider get() {
		if (instance == null) {
			instance = new PlanetNameProvider();
		}
		return instance;
	}

	public List<String> generateUniqueNames(int planetsAmount) {
		List<String> planetNames = new ArrayList<String>();
		for (int i = 0; i < planetsAmount; i++) {
			planetNames.add(getRandomName());
		}
		List<String> uniquePlanetNames = new ArrayList<String>();
		Map<String, Integer> planetNameDuplicates = new HashMap<String, Integer>();
		for (int i = 0; i < planetNames.size(); i++) {
			String name = planetNames.get(i);
			Integer numDuplicates = planetNameDuplicates.get(name);
			if (numDuplicates == null) {
				numDuplicates = 0;
			}
			numDuplicates++;
			planetNameDuplicates.put(name, numDuplicates);
			uniquePlanetNames.add(name + " "
					+ getPlanetNameSuffix(numDuplicates));
		}
		for (int i = 0; i < uniquePlanetNames.size(); i++) {
			String name = planetNames.get(i);
			Integer numDuplicates = planetNameDuplicates.get(name);
			if ((numDuplicates == null) || (numDuplicates <= 1)) {
				uniquePlanetNames.set(i, planetNames.get(i));
			}
		}
		return uniquePlanetNames;
	}

	private String getRandomName() {
		return planetNames[(int) (Math.random() * (planetNames.length - 1))];
	}

	private String getPlanetNameSuffix(int index) {
		if ((index < 1) || (index > greekDigits.length)) {
			return Integer.toString(index);
		}
		return greekDigits[index - 1];
	}
}