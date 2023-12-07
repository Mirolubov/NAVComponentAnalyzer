# NAV-Component-Analyzer
Can use it for exported text code files from Navision 09 - Navision 2018.

## ENU
This program is a C/AL code analyzer and browser.
Can be used in console mode in CI/CD pipeline
Allows you to search for procedure calls and specified antipatterns, such as:
1. Opening a transaction in the Validate tag
2. Calling UI (MESSAGE, RUNMODAL) during a transaction
3. Lack of translation into required languages

### Execute GUI:
1. Build jar artifact
2. Run as: java -jar ./NavComponentAnalizer.jar

### Execute console:
1. Build jar artifact
2. Run as: java -jar ./NavComponentAnalyzer.jar -f c:\Temp -n CU1204.txt CU50001.txt TAB50000.txt -captionml
3. exit code = 3 if found antipatterns

### Console arguments:
* -f             folder to search files
* -n             files to load
* -cs            set charset Name
* -captionml     enable console caption check
* -validate      enable console transaction in validate check
* -?             show help

## RUS
Данная программа представляет из себя анализатор и браузер кода C/AL.
Можно использовать в режиме консоли для анализа кода в CI/CD pipeline.
Позволяет искать вызовы процедур и заданные антипаттерны, такие как:
1. Открытие транзакции в теге Validate
2. Вызов UI (MESSAGE, RUNMODAL) во врем транзакции.
3. Поиск пропусков в обяхательных переводах
