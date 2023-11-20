-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Хост: 127.0.0.1
-- Время создания: Ноя 20 2023 г., 18:48
-- Версия сервера: 10.4.28-MariaDB
-- Версия PHP: 8.2.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- База данных: `gdps_new`
--

-- --------------------------------------------------------

--
-- Структура таблицы `acccomments`
--

CREATE TABLE `acccomments` (
  `ID` int(11) NOT NULL,
  `username` text NOT NULL,
  `userID` int(11) NOT NULL,
  `comment` text NOT NULL,
  `likes` int(11) NOT NULL,
  `isSpam` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Структура таблицы `blocks`
--

CREATE TABLE `blocks` (
  `ID` int(11) NOT NULL,
  `person1ID` int(11) NOT NULL,
  `person2ID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Структура таблицы `comments`
--

CREATE TABLE `comments` (
  `ID` int(11) NOT NULL,
  `userID` int(11) NOT NULL,
  `userName` text NOT NULL,
  `levelID` int(11) DEFAULT NULL,
  `comment` text NOT NULL,
  `likes` int(11) NOT NULL DEFAULT 0,
  `percent` int(11) NOT NULL DEFAULT 0,
  `isSpam` tinyint(1) NOT NULL DEFAULT 0,
  `isAcc` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Дамп данных таблицы `comments`
--

-- --------------------------------------------------------

--
-- Структура таблицы `friendreqs`
--

CREATE TABLE `friendreqs` (
  `ID` int(11) NOT NULL,
  `person1ID` int(11) NOT NULL COMMENT 'from',
  `person2ID` int(11) NOT NULL COMMENT 'to',
  `comment` text NOT NULL,
  `isNew` tinyint(1) NOT NULL COMMENT 'робтор ты дундук'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Дамп данных таблицы `friendreqs`
--

-- --------------------------------------------------------

--
-- Структура таблицы `friendships`
--

CREATE TABLE `friendships` (
  `ID` int(11) NOT NULL,
  `person1ID` int(11) NOT NULL,
  `person2ID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Дамп данных таблицы `friendships`
--

-- --------------------------------------------------------

--
-- Структура таблицы `levels`
--

CREATE TABLE `levels` (
  `levelID` int(11) NOT NULL,
  `levelName` varchar(255) NOT NULL DEFAULT 'Unknown',
  `description` text NOT NULL DEFAULT 'aGkgdXNlcnM=',
  `levelString` longtext NOT NULL,
  `version` int(11) NOT NULL DEFAULT 1,
  `authorID` int(11) NOT NULL,
  `authorGjp` text NOT NULL,
  `authorName` text NOT NULL,
  `difficultyDenominator` int(11) NOT NULL DEFAULT 0,
  `difficultyNumerator` int(11) NOT NULL DEFAULT 0,
  `downloads` int(11) NOT NULL DEFAULT 0,
  `officialSong` int(11) NOT NULL DEFAULT 0,
  `songID` int(11) NOT NULL,
  `audioTrack` int(11) NOT NULL DEFAULT 0,
  `gameVersion` int(11) NOT NULL DEFAULT 21,
  `likes` int(11) NOT NULL DEFAULT 0,
  `length` int(11) NOT NULL,
  `dislikes` int(11) NOT NULL DEFAULT 0,
  `demon` tinyint(1) NOT NULL DEFAULT 0,
  `stars` int(11) NOT NULL DEFAULT 0,
  `featureScore` int(11) NOT NULL DEFAULT 0,
  `auto` int(11) NOT NULL DEFAULT 0,
  `password` int(11) NOT NULL DEFAULT 0,
  `uploadDate` varchar(64) NOT NULL DEFAULT '0',
  `updateDate` varchar(64) NOT NULL DEFAULT '0',
  `copiedID` int(11) NOT NULL,
  `twoPlayer` tinyint(1) NOT NULL DEFAULT 0,
  `customSongID` int(11) NOT NULL DEFAULT 0,
  `coins` int(11) NOT NULL DEFAULT 0,
  `verifiedCoins` tinyint(1) NOT NULL DEFAULT 0,
  `starsRequested` int(11) NOT NULL DEFAULT 0,
  `lowDetailMode` tinyint(1) NOT NULL DEFAULT 0,
  `dailyNumber` int(11) NOT NULL DEFAULT 0,
  `epic` tinyint(1) NOT NULL DEFAULT 0,
  `demonDifficulty` int(11) NOT NULL DEFAULT 0,
  `isGauntlet` int(11) NOT NULL DEFAULT 0,
  `objects` int(11) NOT NULL,
  `unlisted` tinyint(1) NOT NULL,
  `ldm` tinyint(1) NOT NULL DEFAULT 0,
  `editorTime` int(11) NOT NULL DEFAULT 0,
  `settingsString` mediumtext NOT NULL DEFAULT 'aGkgdXNlcnM='
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Дамп данных таблицы `levels`
--

-- --------------------------------------------------------

--
-- Структура таблицы `messages`
--

CREATE TABLE `messages` (
  `ID` int(11) NOT NULL,
  `person1ID` int(11) NOT NULL COMMENT 'from',
  `person2ID` int(11) NOT NULL COMMENT 'to',
  `subject` text NOT NULL,
  `body` text NOT NULL,
  `isNew` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Структура таблицы `quests`
--

CREATE TABLE `quests` (
  `ID` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  `amount` int(11) NOT NULL,
  `reward` int(11) NOT NULL,
  `name` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

--
-- Дамп данных таблицы `quests`
--

INSERT INTO `quests` (`ID`, `type`, `amount`, `reward`, `name`) VALUES
(11, 1, 200, 5, 'Orbs Finder'),
(12, 1, 500, 10, 'Orbs Collector'),
(13, 1, 1000, 15, 'Orbs Master'),
(14, 3, 5, 5, 'Stars Finder'),
(15, 3, 10, 10, 'Stars Collector'),
(16, 3, 15, 15, 'Stars Master'),
(17, 2, 2, 5, 'Coin Finder'),
(18, 2, 4, 10, 'Coin Collector'),
(19, 2, 6, 15, 'Coin Master');

-- --------------------------------------------------------

--
-- Структура таблицы `scores`
--

CREATE TABLE `scores` (
  `scoreID` int(11) NOT NULL,
  `accountID` int(11) NOT NULL,
  `levelID` int(11) NOT NULL DEFAULT 0,
  `percent` int(11) NOT NULL DEFAULT 0,
  `uploadDate` int(11) NOT NULL DEFAULT 0,
  `attempts` int(11) NOT NULL DEFAULT 0,
  `coins` int(11) NOT NULL DEFAULT 0,
  `clicks` int(11) NOT NULL DEFAULT 0,
  `time` int(11) NOT NULL DEFAULT 0,
  `progresses` text NOT NULL DEFAULT '0',
  `dailyID` int(11) NOT NULL DEFAULT 0,
  `isAcc` tinyint(1) NOT NULL DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Структура таблицы `songs`
--

CREATE TABLE `songs` (
  `ID` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `artistID` int(11) NOT NULL DEFAULT 1,
  `artistName` varchar(255) NOT NULL,
  `size` decimal(10,0) NOT NULL DEFAULT 3,
  `videoID` varchar(255) NOT NULL DEFAULT 'aHR0cHM6Ly93d3cueW91dHViZS5jb20vd2F0Y2g_dj1GTlhsV0ZNOTJHYw',
  `youtubeURL` varchar(255) NOT NULL DEFAULT 'UCejLri1RVC7kj8ZVNX2a53g',
  `isVerified` tinyint(1) NOT NULL DEFAULT 1,
  `songPriority` int(11) NOT NULL DEFAULT 0,
  `link` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Дамп данных таблицы `songs`
--

INSERT INTO `songs` (`ID`, `name`, `artistID`, `artistName`, `size`, `videoID`, `youtubeURL`, `isVerified`, `songPriority`, `link`) VALUES
(1, 'ushel za molokom ^_^', 1, 'dekma', 5, 'aHR0cHM6Ly93d3cueW91dHViZS5jb20vd2F0Y2g_dj1GTlhsV0ZNOTJHYw', 'UCejLri1RVC7kj8ZVNX2a53g', 1, 0, 'https://dl.dropboxusercontent.com/s/r22e1bao10d1gcj/ushelzamolokom.mp3'),

-- --------------------------------------------------------

--
-- Структура таблицы `users`
--

CREATE TABLE `users` (
  `userName` varchar(16) DEFAULT NULL,
  `userID` int(11) NOT NULL,
  `stars` int(11) NOT NULL DEFAULT 0,
  `demons` int(11) NOT NULL DEFAULT 0,
  `ranking` int(11) NOT NULL DEFAULT 0,
  `creatorpoints` int(11) NOT NULL DEFAULT 0,
  `iconID` int(11) NOT NULL DEFAULT 0,
  `playerColor` int(11) NOT NULL DEFAULT 0,
  `playerColor2` int(11) NOT NULL DEFAULT 0,
  `secretCoins` int(11) NOT NULL DEFAULT 0,
  `iconType` int(11) NOT NULL DEFAULT 0,
  `special` int(11) NOT NULL DEFAULT 0,
  `usercoins` int(11) NOT NULL DEFAULT 0,
  `messageState` int(11) NOT NULL DEFAULT 0,
  `friendsState` int(11) NOT NULL DEFAULT 0,
  `commentsState` int(11) NOT NULL DEFAULT 0,
  `youtube` text NOT NULL DEFAULT 'https://www.youtube.com/watch?v=FNXlWFM92Gc',
  `accIcon` int(11) NOT NULL DEFAULT 0,
  `accBall` int(11) NOT NULL DEFAULT 0,
  `accBird` int(11) NOT NULL DEFAULT 0,
  `accShip` int(11) NOT NULL DEFAULT 0,
  `accDart` int(11) NOT NULL DEFAULT 0,
  `accRobot` int(11) NOT NULL DEFAULT 0,
  `accGlow` int(11) NOT NULL DEFAULT 0,
  `isRegistered` int(11) NOT NULL DEFAULT 0,
  `globalRank` int(11) NOT NULL DEFAULT 0,
  `messages` int(11) NOT NULL DEFAULT 0,
  `friendRequests` int(11) NOT NULL DEFAULT 0,
  `newFriends` int(11) NOT NULL DEFAULT 0,
  `NewFriendRequest` int(11) NOT NULL DEFAULT 0,
  `age` int(11) NOT NULL DEFAULT 0,
  `accSpider` int(11) NOT NULL DEFAULT 0,
  `twitter` text NOT NULL DEFAULT 'https://www.youtube.com/watch?v=FNXlWFM92Gc',
  `twitch` text NOT NULL DEFAULT 'https://www.youtube.com/watch?v=FNXlWFM92Gc',
  `diamonds` int(11) NOT NULL DEFAULT 0,
  `orbs` int(11) NOT NULL DEFAULT 0,
  `accExplosion` int(11) NOT NULL DEFAULT 0,
  `modType` int(11) NOT NULL DEFAULT 0,
  `commentHistoryState` int(11) NOT NULL DEFAULT 0,
  `gjp` text NOT NULL,
  `gjp2` text NOT NULL,
  `gjpSalt` text DEFAULT NULL,
  `email` text NOT NULL,
  `completedLevels` int(11) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Дамп данных таблицы `users`
--

--
-- Индексы сохранённых таблиц
--

--
-- Индексы таблицы `acccomments`
--
ALTER TABLE `acccomments`
  ADD PRIMARY KEY (`ID`);

--
-- Индексы таблицы `blocks`
--
ALTER TABLE `blocks`
  ADD PRIMARY KEY (`ID`);

--
-- Индексы таблицы `comments`
--
ALTER TABLE `comments`
  ADD PRIMARY KEY (`ID`);

--
-- Индексы таблицы `friendreqs`
--
ALTER TABLE `friendreqs`
  ADD PRIMARY KEY (`ID`);

--
-- Индексы таблицы `friendships`
--
ALTER TABLE `friendships`
  ADD PRIMARY KEY (`ID`);

--
-- Индексы таблицы `levels`
--
ALTER TABLE `levels`
  ADD PRIMARY KEY (`levelID`);

--
-- Индексы таблицы `messages`
--
ALTER TABLE `messages`
  ADD PRIMARY KEY (`ID`);

--
-- Индексы таблицы `scores`
--
ALTER TABLE `scores`
  ADD PRIMARY KEY (`scoreID`);

--
-- Индексы таблицы `songs`
--
ALTER TABLE `songs`
  ADD PRIMARY KEY (`ID`);

--
-- Индексы таблицы `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`userID`),
  ADD UNIQUE KEY `userName` (`userName`);

--
-- AUTO_INCREMENT для сохранённых таблиц
--

--
-- AUTO_INCREMENT для таблицы `acccomments`
--
ALTER TABLE `acccomments`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT для таблицы `blocks`
--
ALTER TABLE `blocks`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT для таблицы `comments`
--
ALTER TABLE `comments`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT для таблицы `friendreqs`
--
ALTER TABLE `friendreqs`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT для таблицы `friendships`
--
ALTER TABLE `friendships`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT для таблицы `levels`
--
ALTER TABLE `levels`
  MODIFY `levelID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT для таблицы `messages`
--
ALTER TABLE `messages`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT для таблицы `scores`
--
ALTER TABLE `scores`
  MODIFY `scoreID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT для таблицы `songs`
--
ALTER TABLE `songs`
  MODIFY `ID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT для таблицы `users`
--
ALTER TABLE `users`
  MODIFY `userID` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
