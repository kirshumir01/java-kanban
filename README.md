# Java-Kanban

## Описание
___

Веб-приложение для сохранения и отслеживания исполнения задач, структурированных по иерархии (задачи, подзадачи, события)
с возможностью сохранения данных в csv-файлы и восстановлением данных после перезапуска приложения.

![](https://github.com/kirshumir01/java-kanban/blob/main/java-kanban.png)

## Структура проекта и функциональность
___

- **задачи (tasks):**
    - **_модель данных_**: название, описание, статус, тип, время начала, продолжительность;
    - **_валидация данных_**: проверка пересечений по времени с другими задачами;
    - **_действия_**: добавление, обновление, получение и удаление по id;
    - **_функциональность_**: сохранение истории задач в csv-файл и восстановление данных из истории после перезапуска приложения.

- **подзадачи (subtasks):**
    - **_модель данных_**: название, описание, статус, тип, время начала, продолжительность, id события;
    - **_валидация данных_**: проверка пересечений по времени с другими задачами;
    - **_действия_**: добавление, обновление, получение и удаление по id, получение всех подзадач по id события;
    - **_функциональность_**: сохранение истории задач в csv-файл и восстановление данных из истории после перезапуска приложения.

- **события (epics):**
    - **_модель данных_**: название, описание, статус, тип, время начала, продолжительность, список id подзадач, время завершения;
    - **_валидация данных_**: проверка пересечений по времени с другими задачами;
    - **_действия_**: добавление, обновление, получение и удаление по id;
    - **_функциональность_**: сохранение истории задач в csv-файл и восстановление данных из истории после перезапуска приложения.

- **статусы задач**:
    - ```NEW``` — новая;
    - ```IN PROGRESS``` — в процессе;
    - ```DONE``` — завершена.

Взаимодействие пользователя с приложением реализовано через HttpServer.
Приложение обрабатывает HTTP-запросы пользователя по соответствующим эндпоинтам и формирует HTTP-ответы с сериализацией/десирализацией данных в формате JSON.
Взаимодействие предусмотрено через порт ```8080```.

Схема API приложения представлена ниже:

![](https://github.com/kirshumir01/java-kanban/blob/main/java-kanban-api.png)

## Инструменты и технологии
___

- версия Java: 21
- HttpServer
- JSON
- взаимодействие клиента и сервера помощью HTTP-запросов и HTTP-ответов через порт ```8080```.
- хранение данных (рабочий режим): оперативная память 
- хранение данных (режим тестирования): оперативная память
- тестирование: JUnit 5 (покрытие классов - 82 %)

## Инструменты для запуска приложения
___

- Java Development Kit (JDK) - версия 21 или более поздняя

### Инструкции по установке JDK

Ниже приведены инструкции по установке JDK 21 на различных операционных системах.

<br>

<details>

<summary> Установка на macOS </summary>

Установите Homebrew запуском следующей команды в терминале (командной строке) операционной системы:

```shell
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```

Установите JDK 21 с помощью Homebrew:

```shell
brew install openjdk@21
```

Создайте символическую ссылку, чтобы система могла найти JDK:

```shell
sudo ln -sfn /opt/homebrew/opt/openjdk@21/libexec/openjdk.jdk /Library/Java/JavaVirtualMachines/openjdk-21.jdk
```

Добавьте JDK 21 в PATH. Откройте файл .zshrc (или .bash_profile, в зависимости от используемой оболочки) и добавьте следующую строку:

```
echo 'export PATH="/opt/homebrew/opt/openjdk@21/bin:$PATH"' >> ~/.zshrc
```

Перезагрузите терминал или примените изменения с помощью команды:

```shell
source ~/.zshrc
```

Проверьте установленную версию Java:

```shell
java -version
```
</details>

<br>

<details>

<summary> Установка на Linux </summary>

Откройте терминал и выполните команду для обновления списка пакетов:

```shell
sudo apt update
```

Установите JDK 21:

```shell
sudo apt install openjdk-21-jdk
```

Убедитесь, что JDK установлен и настроен корректно:

```shell
java -version
```

</details>

<br>

<details>

<summary> Установка на Windows </summary>

1. Скачайте установочный файл JDK 21 с официального сайта [Oracle](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html) или OpenJDK.

2. Запустите установочный файл и следуйте инструкциям установщика.

3. После установки настройте переменную среды JAVA_HOME:
- откройте ```Системные настройки``` > ```Переменные среды```;
- в разделе ```Системные переменные``` нажмите ```Создать``` и введите:
  - имя переменной: ```JAVA_HOME```
  - значение переменной: путь к установленной JDK (например, ```C:\Program Files\Java\jdk-21```).
- добавьте ```JAVA_HOME\bin``` в переменную Path.

4. Проверьте версию Java в командной строке:

```shell
java -version
```

</details>

## Подготовка к запуску приложения
___

**ВАЖНО!** Запуск приложения осуществляется через системный порт ```8080```. Перед запуском убедитесь, что порт ```8080``` свободен!

### Как проверить, что системный порт свободен?

Ниже приведены инструкции по проверке порта на различных операционных системах.

<br>

<details>

<summary> Проверка порта на macOS </summary>

Откройте терминал.

Выполните следующую команду, заменив ```PORT``` на номер порта, который нужно проверить:

```
lsof -i :PORT
```

Если порт занят, команда выведет список процессов, использующих порт. Для завершения процесса используйте команду:

```
kill -9 PID
```
где ```PID``` — идентификатор процесса из вывода предыдущей команды.

Если порт свободен, команда не вернет никаких данных.

</details>

<br>

<details>

<summary> Проверка порта на Linux </summary>

Откройте терминал.

Выполните следующую команду, заменив ```PORT``` на номер порта:

```
sudo lsof -i :PORT
```

Если порт занят, команда выведет список процессов, использующих порт. Для завершения процесса используйте команду:

```
sudo kill -9 PID
```
где ```PID``` — идентификатор процесса из вывода предыдущей команды.

Если порт свободен, команда не вернет никаких данных.

</details>

<br>

<details>

<summary> Проверка порта на Windows </summary>

Откройте командную строку (cmd) или PowerShell с правами администратора.

Выполните следующую команду, заменив ```PORT``` на номер порта:

```
netstat -aon | findstr :PORT
```

Если порт занят, команда выведет информацию о процессе, использующем порт. Обратите внимание на PID (идентификатор процесса).

Чтобы завершить процесс, откройте ```Диспетчер задач```, перейдите на вкладку ```Подробности```, найдите процесс с соответствующим PID и завершите его.

Если порт свободен, команда не вернет никаких данных.

</details>

### Где следует выполнять команды для запуска приложения?

- в терминале (командной строке) операционной системы;
- в программной среде разработки [IntelliJ IDEA](https://www.jetbrains.com/ru-ru/idea/download/other.html) непосредственно в данном файле нажатием на символ тройной стрелки ```>>>```, расположенной слева от строки описания соответствующей команды.

## Запуск приложения
___

1. Склонируйте репозиторий с использованием следующей команды:

```shell
git clone https://github.com/kirshumir01/java-kanban.git
```

2. Перейдите в корневую директорию склонированного проекта и выполните команду:

```shell
cd java-kanban/src/ru/yandex/practicum/services/server
```

3. Запустите приложение командами:

```shell
javac HttpTaskServer.java
```

```shell
java HttpTaskServer
```

Доступ к публичному API будет открыт по ссылке через адресную строку интернет-браузера: `http://localhost:8080`.
