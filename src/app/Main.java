package app;

import java.util.logging.Level;
import java.util.logging.Logger;

import cli.InterfacciaCLI;
import config.ConfigurazioneGlobale;
import config.TipoInterfaccia;
import config.TipoPersistenza;
import controller.grafico.LoginControllerGrafico;
import controller.grafico.PrenotazioneControllerGrafico;
import dao.DAOFactory;
import dao.database.DAOFactoryDatabase;
import dao.filesystem.DAOFactoryFileSystem;
import gui.GuiJavaFXApp;
import notifica.NotificatorePrenotazioni;
import notifica.OsservatoreEmailCliente;
import notifica.OsservatoreRegistroAgenzia;
import payment.FacadePagamento;

public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] argomenti) {
        ConfigurazioneGlobale configurazione = ConfigurazioneGlobale.getSingletonInstance();
        LOGGER.log(Level.INFO, "Avvio con persistenza {0} e interfaccia {1}.",
                new Object[] { configurazione.getPersistenza(), configurazione.getInterfaccia() });

        DAOFactory daoFactory = creaFamiglia(configurazione.getPersistenza());

        NotificatorePrenotazioni notificatore = new NotificatorePrenotazioni();
        notificatore.registraOsservatore(new OsservatoreEmailCliente(notificatore));
        notificatore.registraOsservatore(new OsservatoreRegistroAgenzia(notificatore));

        LoginControllerGrafico loginControllerGrafico = new LoginControllerGrafico(daoFactory);
        PrenotazioneControllerGrafico prenotazioneControllerGrafico =
                new PrenotazioneControllerGrafico(daoFactory, new FacadePagamento(), notificatore);

        if (configurazione.getInterfaccia() == TipoInterfaccia.CLI) {
            new InterfacciaCLI(loginControllerGrafico, prenotazioneControllerGrafico).avvia();
        } else {
            GuiJavaFXApp.avvia(argomenti, loginControllerGrafico, prenotazioneControllerGrafico);
        }
    }

    private static DAOFactory creaFamiglia(TipoPersistenza persistenza) {
        if (persistenza == TipoPersistenza.DATABASE) {
            return new DAOFactoryDatabase();
        }
        return new DAOFactoryFileSystem();
    }
}
