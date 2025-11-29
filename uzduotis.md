Tema: Kuriama maisto rezervavimo ir valdymo sistema. Vartotojai yra 4 tipų - klientai, restoranų savininkai, maistą pristatantys vairuotojai ir administratoriai. Restoranų savininkai prižiūri meniu bei mato užsakymus, vartotojai užsako norimą maistą (seka užsakymo statusą, gali parašyti restoranui ir vairuotojui, maistą pristatantys gali paimti užsakymą. Administratoriai prieina prie visko ir sprendžia problemas. Sistemos realizavimui keliami pagrindiniai reikalavimai:

Sistema turi vartotojus, kurie prisijungdami prie sistemos autentifikuojasi ir pasiekia tik jiems skirtus duomenis. Tikslas - apriboti matomumą bei funkcionalumą pagal vartotojo tipą; [0.5]
Desktop skirta restoranams ir administratoriams. Mobili programėlė - klientams ir vairuotojams; [0.5]
Vartotojai yra 4 tipų (klientai, restoranų savininkai, maistą pristatantys vairuotojai ir administratoriai). Skiriasi vartotojų duomenys, naudoti paveldėjimą ir roles. Jungiamasi su prisijungimo vardu ir slaptažodžiu, slaptažodis duomenų bazėje turi būti hashed; [0.5]
Klientai mato restoranų sąrašus bei susideda savo užsakymų krepšelį iš pasirinkto restorano, gali užsisakyti. Restoranai kelia savo patiekalus ir paima užsakymus (statuso keitimas, sekimas), maistą pristatantys paima. [0.25]
Viską redaguoja administratoriai. [0.25]
Galimas susirašinėjimas prie užsakymo (tiek su restoranų, tiek vairuotoju)
Galima palikti atsiliepimus ir įvertinimus apie restoraną, vairuotoją. Savo ruožtu atsiliepimai paliekami apie klientus (skirta tik vairuotojams, ir restoranams)
Galima filtruoti pagal įvairius požymius (bent 5 filtrai)
Kainos modifikuojamos pagal laiką t.y. populiariu metu brangiau, ne populiariu pigiau.
Ištikimų klientų bonus taškų kaupimo sistema.
Reikalavimus detalizuosiu dar labiau semestro eigoje.

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

* Jei norite gauti maksimalų taškų skaičių, turite įgyvendinti CRUD visoms klasėms. Pavyzdžiui, jei jūsų projekte yra 7 klasės, o 3 klasėms realizuojate kūrimo funkcionalumą, gausite 2,5/7*3 = 1,07 taškus