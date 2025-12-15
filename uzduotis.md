Tema: Kuriama maisto rezervavimo ir valdymo sistema. Vartotojai yra 4 tipų - klientai, restoranų savininkai, maistą pristatantys vairuotojai ir administratoriai. Restoranų savininkai prižiūri meniu bei mato užsakymus, vartotojai užsako norimą maistą (seka užsakymo statusą, gali parašyti restoranui ir vairuotojui, maistą pristatantys gali paimti užsakymą. Administratoriai prieina prie visko ir sprendžia problemas. Sistemos realizavimui keliami pagrindiniai reikalavimai:

Sistema turi vartotojus, kurie prisijungdami prie sistemos autentifikuojasi ir pasiekia tik jiems skirtus duomenis. Tikslas - apriboti matomumą bei funkcionalumą pagal vartotojo tipą. Desktop naudojasi tik administratoriai ir restoranai. Android programėlė skirta tik klientams ir vairuotojams [0.5]
Vartotojai yra 4 tipų (klientai, restoranų savininkai, maistą pristatantys vairuotojai ir administratoriai). Skiriasi vartotojų duomenys, naudoti paveldėjimą ir roles. Jungiamasi su prisijungimo vardu ir slaptažodžiu, slaptažodis duomenų bazėje turi būti hashed; [0.5]
Administratoriai turi visas redagavimo teises - CRUD vartotojams, užsakymams, patiekalams, susirašinėjimams. Prieina prie visų langų ir funkcionalumų. [0.25]
Filtravimas pagal įvairius požymius [1]:
Adminai gali ieškoti pagal vartotojo login, name, surname ir pan. (bent 4 požymiai)
Restoranai gali ieškoti tarp savo užsakymų pagal užsakymo statusą, užsakymo laiką, vartotoją ir pan.
Adminai gali ieškoti tarp užsakymų papildomai pagal restoraną.
Restoranai kelia savo patiekalus ir priima užsakymus (keičiasi užsakymo statusas) [1]
Negali keisti pirkėjo, vairuotojo, jei jau užsakymas paimtas
Gali neleisti ir vairuotojo pridėti, jei nieko nėra. Čia laisvas pasirinkimas kaip logiškiau.
Gali išimti iš užsakymo patiekalus, tada persiskaičiuoja kaina užsakymo.
Restoranai gali matyti susirašinėjimą ir rašyti. Redagavimo teisių neturi. [0.25]
Klientai programėlėje mato restoranų sąrašus bei susideda savo užsakymų krepšelį iš pasirinkto restorano, gali užsisakyti [1]. 
Ant patiekalo paspaudus ar iš karto turi matytis aprašymas apie patiekalą.
Ant restorano paspaudus (prie patiekalų) ar iš karto sąraše turi matytis restorano info (darbo laikas, restorano tipas ir pan.)
Vairuotojai mato nepaimtus užsakymus ir gali pasiimti užsakymą pristatymui. Kai pristato - pakeičia statusą į completed. [1]
Galimas susirašinėjimas prie užsakymo - gali rašyti restoranas, klientas ir vairuotojas. Adminas tik redaguoja arba trina. [1]
Galima palikti atsiliepimus ir įvertinimus apie restoraną, vairuotoją. Savo ruožtu atsiliepimai paliekami apie klientus (skirta tik vairuotojams, ir restoranams) [1]
Klientai ir vairuotojai kuria paskyras per mobiliąją programėlę, gali redaguoti savo info. [1]
Klientai ir vairuotojai gali peržiūrėti savo užsakymus. Susirašinėjimas nebegalimas prie atliktų užsakymų, galima tik susirašinėjimo peržiūra.[1]
Kainos modifikuojamos pagal laiką t.y. populiariu metu brangiau, ne populiariu pigiau.[0.5]
Ištikimų klientų bonus taškų kaupimo sistema.[0.5]

LAB1

1.      Remiantis kursinio darbo užduotimis, suprojektuoti sistemos klasių diagramą, kuri atitiktų objektinio programavimo principus ir leistų paprastai kurti ir valdyti visus projekto objektus. [2 balai]

2.      Aprašyti visas suprojektuotas klases, su tinkamai parinktais klasių kintamaisiais, jų tipais. Turi būti tarpusavyje besisiejančios (pageidautinas kompozicijos ar agregacijos ryšys) klasės. [1 balas]

Kiekvienai iš sukurtų klasių reikia realizuoti CRUD (C – create, naujo objekto kūrimas ar sudedamojo objekto pridėjimas; R – read, duomenų apie objektą gavimas; U – update, objekto duomenų atnaujinimas; D – delete, objekto šalinimas (ne tiek objekto naikinimas, kiek jį sudarančių kitų objektų šalinimas)) funkcijas. Vartotojui turi būti leidžiama valdyti (vykdyti CRUD funckijas) su visais sistemos objektais.

Reikia sukurti grafinę vartotojo sąsają. Grafinės sąsajos išvaizda gali būti kokia norite, bet turėtų būti panaudoti šie komponentai:

3.      Vienas pagrindinis langas ir bent po 1 iššokantį (perspėjimo ar klaidos pranešimą, spalvos ar failo pasirinkimo dialogą) ir 1 papildomą langą. [1 balas]

4.      Panaudotas meniu arba tab'ų juosta, atskirų sistemos dalių valdymui. [1 balas]

5.      Naudojami standartiniai elementai, tokie kaip duomenų įvedimo laukai, mygtukai ir pan. [1 balas]

6.      Panaudoti sudėtingesni grafiniai elementai, tokie kaip lentelė, pasirinkimų sąrašas (su sugeneruotomis iš turimų duomenų reikšmėmis, o ne statinėmis) ir pan. [1 balas]

7.      Realizuota duomenų įvedimo kontrolė ir klaidų pranešimai. [1 balas]

8.      CRUD funkcionalumas grafinėje vartotojo sąsajoje. [2 balai]

LAB2

Turimai GUI, Kiekvienai iš sukurtų klasių reikia realizuoti backend CRUD (C – create, naujo objekto kūrimas ar sudedamojo objekto pridėjimas; R – read, duomenų apie objektą gavimas; U – update, objekto duomenų atnaujinimas; D – delete, objekto šalinimas (ne tiek objekto naikinimas, kiek jį sudarančių kitų objektų šalinimas)) funkcijas. Vartotojui turi būti leidžiama valdyti (vykdyti CRUD funckijas) su visais sistemos objektais.

Reikia papildyti grafinę vartotojo sąsają, kad būtų „pajungta“ duomenų bazių logika:

1.      Prisijungimas prie duomenų bazės ir atsijungimas nuo jos. [1.25 balai]

2.      Duomenų gavimas iš duomenų bazės (tik pilno, tiek ir filtruoto, atrinkto). [1.25 balai]

3.      Duomenų įrašymas į duomenų bazę. [2.5 balo]

4.      Duomenų šalinimas iš duomenų bazės. [2.5 balo]

5.      Duomenų redagavimas duomenų bazėje. [2.5 balo]

LAB3

Realizuoti servisą (REST full API), kuris turėtų kelias pagrindines funkcija:

1. Web serviso veikimas (galima kreiptis ir gauti atsakymą). [1 balas]

2. Duomenų gavimas html, json ar kitu formatu. [2 balai]

3. Parametrų, duomenų perdavimas į serverį. [2 balai]

4. Realizuoti CRUD funkcijas vairuotojui ir klientui pagal kursinio darbo užduotį. [5 balai]

Per Intellij pasirenkame Spring Initializr tipo projektą (File->New->Project->Spring Initializr)

Antrame lange Jūsų paklaus kokių bibliotekų reikės. Iš karto pasirinkite šias - Spring Web, Spring HATEOAS, Spring Data JPA. Tie kas norėsit su JDBC dirbti, pasirenkate arba JDBC API/Spring Data JDBC. 

SpringBoot leidžia sukurti savarankiškas (stand-alone), produkcinio lygmens Spring'u paremtas aplikacijas. SpringBoot jau turi Tomcat, Jetty ar Undertow t.y. nereikia deploy'inti war failų kaip kad reikėjo, kai darėme kitu būdu. Jis suteikia tam tikras pradžios(starting) priklausomybes ir palengvina projekto kūrimą (Build). Taip pat nereikia generuoti tam tikro kodo ir konfigūruoti XML failų. Jei pasižiūrėsite į projekto struktūrą web.xml net nėra. SpringBoot aplikacija yra 100% pure Java.

Taigi dirbant su SpringBoot turime tokią struktūrą (main metodą turinti klasė):

1) Mums automatiškai sukuriama Application klasė, pažymėta @SpringBootApplication anotacija. Ši anotacija automatiškai prideda @Configuration, @EnableAutoConfiguration, @ComponentScan (Galite pasižiūrėti, jei ant @SpringBootApplication paspausite Ctrl+left Mouse per Intellij).

@Configuration priskiria klasę kaip šaltinį/resursą (bean) aplikacijos kontekste. @EnableAutoConfiguration pasako, kad reikia pridėti beans esančius classpath'e, kitus beans ir įvairius nustatymus. Pvz., jei turime spring-webmvc classpath pridėtą, tai ši anotacija "pagauna" path, kad turime web app ir įjungia tam tikras elgsenas kaip kad sukonfigūruoja/užsettina Dispatcher Servlet. @ComponentScan liepia Spring ieškoti komponentų, konfigūracijų, servisų ir kt.

Kai jau turite susikūrę projektą, iš savo 1LD parsineškite visą katalogą, kur yra Jūsų klasės. Mums taip pat reikės duomenų bazės ir joje esančių duomenų. Logiška būtų prisidėti prie šio projekto persistence, turėti persistence.xml failiuką, kurį turėjome 1LD, hibernate kontrolerių klases ir toliau dirbti. Tačiau SpringBoot  by default ignoruoja persistence.xml ir orm.xml failus. Jei norime, kad juos naudotų, turime aprašyti savo bean su @Bean anotacija, kuri būtų LocalEntityManagerFactoryBean (su ID mūsų entityManagerFactory ir ten nustatyti persistence unit). SpringBoot šitą persistence problemą išsprendžia už Jus. Prie application.properties nurodome prisijungimus (iš esmės tie patys duomenys kaip persistence.xml, tik kad nereikia persistence unit, class nurodyti):

spring.datasource.url=jdbc:mysql://localhost:3306/courseworkFour
spring.datasource.username=root
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.generate-ddl=true
spring.jpa.hibernate.ddl-auto = update
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
Tada reikia susikurti repozitoriją, kuri saugotų norimų objektų sąrašus. Tam susikuriame atskiras repozitorijas, atkiroms klasėms. Aš pavadinau UserRepo, BookRepo ir t.t. Repozitorija yra interfeisas, kuris paveldi JpaRepository klasę:


public interface UserRepository extends JpaRepository<User, Integer> {
}

Turite matyti žalio bean simboliuką. Viskas, tiek su persistence Jūsų User klasei. Jei paspausite ctrl+left mouse and JpaRepository, pamatysite pagrindinius metodus, kuriuos už Jus implementuoja SpringBoot. Jei reikia kažkokių specifinių užklausų, tada reiktų pasipildyti, tačiau pagrindinės CRUD operacijos jau yra už Jus sutvarkomos.

Beje, nepamirštam pasitikrinti, ar visos reikiamos klasės yra vietoje (mysql connector, gson ar dar kokių Jums reikia).

Dabar susikuriam Rest kontrolerį, tam sukuriat paprasčiausią klasę ir ją pažymit @RestController anotacija:



Rest kontroleris suhandlins/apdoros HTTP užklausas (request). @Autowired pasako "paimk bean pavadinimu userRepository" (automatiškai Spring'o sugeneruojamas).

Dabar sukuriam metodą ir jam nurodome mapping'ą:


@GetMapping(value = "/allUsers")
public @ResponseBody
Iterable<User> getAll() {
    return userRepo.findAll(); //Read
}
Nurodėme, kad grąžintų @ResponseBody, tai reiškia, kad bus grąžintas String, o ne view pavadinimas. Jei turime tokį kodą:


@GetMapping(value = "validateCLient")
@ResponseBody
//Pls don't, baisu perduoti prisijungimus kaip request param
public Client validateClient(@RequestParam String login, @RequestParam String psw) {
    //Sioje vietoje man reik parsint jei bus string

    return clientRepo.getClientByLoginAndPassword(login, psw);
}
 
Čia nurodytas @RequestParam String login ir psw. Nurodome, kad parametras bus iš GET requesto, prikabintas prie url galo.
@PathVariable - bus kintamojo reikšmė iš karto paduodama per url. Priešingai nei @RequestParam, nebus ?variableName=variableValue url gale.

Jei tarkime kuriate projektą su SpringBoot ir Jūsų DB yra tuščia, galima paleidimo metu įterpti duomenis. Kur mūsų klasė su @SpringBootApplication, prisidedame Logger bei pažymime @Bean anotacija kodą, kurį turėtų įvykdyti paleidimo metu:


private static final Logger logger = LoggerFactory.getLogger(Lab2WebLayerApplication.class);

public static void main(String[] args) {
    SpringApplication.run(Lab2WebLayerApplication.class, args);
}
@Bean
public CommandLineRunner demo(ClientRepo repository) {
    return (args) -> {
        // save a few customers
        repository.save(new Client("a", "a","Testinis", "Testinis", "--", LocalDate.now()));
        repository.save(new Client("a2", "a","Testinis", "Testinis", "--", LocalDate.now()));
        repository.save(new Client("a3", "a","Testinis", "Testinis", "--", LocalDate.now()));
        repository.save(new Client("a4", "a","Testinis", "Testinis", "--", LocalDate.now()));


        logger.info("All clients found with findAll():");
        logger.info("-------------------------------");
        for (Client client : repository.findAll()) {
            logger.info(client.toString());
        }
        logger.info("");

        // fetch person by id
        Optional<Client> person = repository.findById(1);
        logger.info("User found with findById(1L):");
        logger.info("--------------------------------");
        logger.info(person.toString());
        logger.info("");
        logger.info("");
Tokių būdu mums įterpia keletą Client tipo įrašų. Kaip tai vyksta> SpringBoot paleidžia visus CommandLineRunner beans kartą, kai kontekstas yra užkraunamas. Runneriui reikia mūsų userRepository kopijos ir ja naudodamasis prideda objektus į db.

Taigi, norėdami užwrapinti repozitoriją web sluoksniu (web layer) tam turime Spring MVC. Ir SpringBoot tai leidžia lengvai realizuoti funkcijas su minimaliu kiekiu kodo. Dabar truputį detaliau pakomentuosiu anotacijas.
@RestController nurodo, kad duomenys grąžinti kiekvieno metodo bus įrašomi į response body vietoje to, kad būtų sugeneruojamas kažkoks šablonas (view template). Turime anotacijas kiekvienai operacijai @GetMapping, @PostMapping, @PutMapping ir @DeleteMapping, kurie atitinka HTTP GET, POST, PUT ir DELETE kvietimus. 

Kai bandome ištraukti duomenis ar juos įterpti, būtų gerai nurodyti ką daryti klaidų atveju. Pvz, nerado įrašo, gausime kaip vartotojai 404 klaidą. Tačiau tarkime aš noriu labiau praplėsti tą klaidą, kad žinočiau kodėl negavau. Tam tikslui susikuriu klaidos klasę ir metode, kuriame galiu gauti tą klaidą.

1)Susikuriu:
 class UserNotFound extends RuntimeException {
    UserNotFound(Integer id) {
        super("Could not find user " + id);
    }
}
2) Naudoju metode:
@PutMapping(value = "/updateUser/{id}")
public @ResponseBody
String updateUser(@RequestBody String userInfoToUpdate, @PathVariable int id) {
    Gson gson = new Gson();//Helps me parse things from Json quickly
    Properties properties = gson.fromJson(userInfoToUpdate, Properties.class);

    //Issitrauksiu useri pagal id

    //Cia bus useris istrauktas, gali buti, kad jo neras, tai susikuriu sau atskira klaida, kad butu graziai pavaizduota
    Person person = personRepository.findById(id)
            .orElseThrow(() -> new UserNotFound(id));
    person.setName(properties.getProperty("name"));
    person.setSurname(properties.getProperty("surname"));
    person.setCardNumber(properties.getProperty("card"));
    //Pabaigti

    userRepository.save(person);
    return "Success";
}
Kol kas turime pasirašę web-based servisą, kuris atlieka bazines funkcijas su mūsų user duomenimis. Bet to nepakanka, kad būtų RESTful. Gražūs url nėra RESTful, CRUD operacijos nėra REST. Kol kas ką sukūrėme yra RPC (Remote procedure call).
Taip yra todėl, kad nėra kaip žinoti apie sąveiką su servisu. Reiktų dokumentuoti kokie kvietimai galimi.
Kol kas nenaudojome hypermedia mūsų reprezentacijose, todėl klientas PRIVALO harkodinti url navigacijai. Yra toks dalikas kaip SpringHATEOAS (pačioje pradžioje jį mes pasirinkome pridėjimui), skirtas padėti rašyti taip vadinamą hypermedia driven output. 

Jei kartais pradžioje neprisidėjote - einat į pom failą ir prie dependencies įsimetat:

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-hateoas</artifactId>
</dependency>
Ši bibliotekėlė leidžia sugeneruoti konstruktus, leidžiančius apibrėžti RESTful servisą ir pateikti vartotojui geru formatu. Esminis momentas - pridėti nuorodas į svarbias operacijas. Einam kur mūsų GetMapping nustatom, kad grąžintų EntityModel<Person>. EntityModel yra bendrinis konteineris iš SpringHATEOAS, kuris įtraukia ne tik duomenis, bet ir nuorodų rinkinius.


@GetMapping("/client/{id}")
EntityModel<Person> one(@PathVariable Integer id) {

    Client client = clientRepo.findById(id)
            .orElseThrow(() -> new UserNotFound(id));

    return EntityModel.of(client, //
            linkTo(methodOn(UserController.class).one(id)).withSelfRel(),
            linkTo(methodOn(UserController.class).getAll()).withRel("clients"));
}

1) pirmas linkTo: sakom sukonstruok nuorodą į one metodą, kuris pas mus jau yra
2) antras linkTo: sukonstruok nuorodą į getAll metodą, kuris irgi pas mus jau yra.
Vienas iš pagrindinių SpringHATEOAS core tipų yra link, jis turi URI ir rel (relation). Kai pažiūrėsite per Postman, turėsim 2 nuorodas, ant kurių paspaudus, mums pakvies vieną ar kitą metodą. Visas dokumentas bus subuildintas naudojant HAL (hypertext application language).