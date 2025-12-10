# Darbų sąrašas (To-Do List)

## 1. Kritiniai funkciniai trūkumai (Privaloma)

<!-- 1.  **Duomenų bazės uždarymas**
    *   **Ką daryti:** Perrašyti `App.stop()` metodą ir iškviesti `JpaUtil.close()`.
    *   **Kodėl:** Reikalaujama „Prisijungimas ir **atsijungimas**“. -->

<!-- 2.  **Pagalbos bilietų (SupportTicket) sistema**
    *   **Ką daryti:** Sukurti GUI (Tab'ą ir Dialogą) bilietų kūrimui, peržiūrai ir sprendimui.
    *   **Kodėl:** Modelis yra, bet valdymo nėra. -->

3.  **Mokėjimo būdų (PaymentMethod) valdymas**
    *   **Ką daryti:** `UserDialog` lange (klientams) pridėti sąrašą kortelių pridėjimui/šalinimui.
    *   **Kodėl:** Modelis yra, bet valdymo nėra.

4.  **Chat žinučių CRUD**
    *   **Ką daryti:** `ChatDialog` lange pridėti mygtukus „Redaguoti“ ir „Trinti“ (savo žinutėms).
    *   **Kodėl:** Reikalaujama pilno CRUD visoms esybėms.

<!-- 5.  **Dinaminė kainodara (Pricing Rules)**
    *   **Ką daryti:** `OrderDialog` lange, pridedant prekes, tikrinti ir taikyti `PricingRule.apply()`.
    *   **Kodėl:** Logika yra, bet aplikacijoje ignoruojama. -->

6.  **Lojalumo taškų sistema**
    *   **Ką daryti:**
        *   Suteikti taškus, kai užsakymas įvykdomas (`DELIVERED`).
        *   Leisti administratoriui redaguoti taškus per `UserDialog`.
    *   **Kodėl:** Sistema neveikia (taškai nekaupiami).

7.  **Išplėstinis filtravimas**
    *   **Ką daryti:** `MainView` (Orders tab) pridėti laukus filtravimui pagal **Datą** ir **Kainą**.
    *   **Kodėl:** Reikalaujama „bent 5 filtravimo kriterijų“, dabar veikia tik Statusas.

<!-- 8.  **Atsiliepimai (Reviews) vairuotojams/klientams**
    *   **Ką daryti:** Pridėti galimybę rašyti atsiliepimus ne tik restoranams, bet ir Vairuotojams bei Klientams.
    *   **Kodėl:** Reikalavimas nurodo atsiliepimus apie „klientus ir vairuotojus“. -->

<!-- 9.  **Auto refresh po review palikimo kad score atsirastu** -->
<!-- 10. **Klientas neturi galeti istrinti ar keisti order status**
11. **Klientas turi galeti tik sukurti ir uzdaryti support ticketa** -->

## 2. Struktūriniai trūkumai

*   **Mobili aplikacija**: Trūksta mobiliosios versijos (klientams/vairuotojams), kuri minima užduotyje. Šiuo metu yra tik Desktop.
