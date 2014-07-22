/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wati.controller;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import org.primefaces.model.StreamedContent;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.net.URL;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import org.primefaces.model.DefaultStreamedContent;

import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import wati.model.Acompanhamento;
import wati.model.ProntoParaParar;
import wati.model.User;
import wati.persistence.GenericDAO;
import wati.utility.EMailSSL;

/**
 *
 * @author hedersb
 */
@ManagedBean(name = "parouDeFumarController")
@SessionScoped
public class ParouDeFumarController extends BaseController<Acompanhamento> {

    private static final int LIMITE_IGUALDADE_HORAS = 24;
    private String recaida;
    private Acompanhamento acompanhamento;

    private StreamedContent lapsoRecaida;

    /**
     * Creates a new instance of ParouDeFumarController
     */
    public ParouDeFumarController() {
        //super(Acompanhamento.class);
        try {
            this.daoBase = new GenericDAO<Acompanhamento>(Acompanhamento.class);
        } catch (NamingException ex) {
            String message = this.getText("mensagem.erro");
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, message, null));
            Logger.getLogger(ParouDeFumarController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

    }

    public String recaidaOuLapso() {

        Acompanhamento a = this.getAcompanhamento();
        //a.setRecaida(this.recaida.equals("1"));
        try {

            this.getDaoBase().insertOrUpdate(a, this.getEntityManager());

            if (a.getRecaida() == 1) {
                return "parou-de-fumar-acompanhamento-recaidas-identificar-motivos.xhtml";
            } else if(a.getRecaida() == 0){
                return "parou-de-fumar-acompanhamento-lapso.xhtml";
                //"parou-de-fumar-acompanhamento-lapso-identificar-fatores-recaida.xhtml";
            }else if(a.getRecaida() == 2)
                return "parou-de-fumar-acompanhamento-estou-sem-fumar.xhtml";
            else
                return null;

        } catch (SQLException ex) {
            Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    public String identificarMotivos() {

        Acompanhamento a = this.getAcompanhamento();

        try {

            this.getDaoBase().insertOrUpdate(a, this.getEntityManager());

            return "parou-de-fumar-acompanhamento-lapso-identificar-fatores-recaida.xhtml";

        } catch (SQLException ex) {
            Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;

    }

    public String identificarFatoresRecaida() {

        Acompanhamento a = this.getAcompanhamento();

        try {

            this.getDaoBase().insertOrUpdate(a, this.getEntityManager());
            if(isRecaidaLidar11() || isRecaidaLidar22() || isRecaidaLidar33() || isRecaidaSituacao11() || isRecaidaSituacao22() || isRecaidaSituacao33())
                return "parou-de-fumar-acompanhamento-lapso-plano-evitar-recaida.xhtml";
            else
                return "parou-de-fumar-acompanhamento-lapso-plano-evitar-recaida-padrao.xhtml";

        } catch (SQLException ex) {
            Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;

    }

    public void enviarEmail() {

        Object object = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loggedUser");

        if (object == null) {

            Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, this.getText("user.not.logged"));
            //message to the user
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, this.getText("deve.estar.logado"), null));

        } else {

            User user = (User) object;
            Acompanhamento a = this.getAcompanhamento();

            try {
                String from = "watiufjf@gmail.com";
                String to = user.getEmail();
                String subject = this.getText("plano.wati");
                String message = this.getText("dear") + user.getName() + ",\n\n"
                        + this.getText("plano.email") + "\n\n"
                        + "\n" + this.getText("plano.estrategias.email") + "\n";
                if (a.getRecaidaLidar1() != null && !a.getRecaidaLidar1().isEmpty()) {
                    message += a.getRecaidaLidar1() + "\n";
                }
                if (a.getRecaidaLidar2() != null && !a.getRecaidaLidar2().isEmpty()) {
                    message += a.getRecaidaLidar2() + "\n";
                }
                if (a.getRecaidaLidar3() != null && !a.getRecaidaLidar3().isEmpty()) {
                    message += a.getRecaidaLidar3() + "\n";
                }
                message += "\n\n" + this.getText("att") + "\n";

                EMailSSL eMailSSL = new EMailSSL();
                eMailSSL.send(from, to, subject, message);

                Logger.getLogger(ParouDeFumarController.class.getName()).log(Level.INFO, this.getText("plano.enviado") + user.getEmail() + ".");
                //message to the user
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, this.getText("email.enviado"), null));

            } catch (Exception ex) {

                Logger.getLogger(ParouDeFumarController.class.getName()).log(Level.SEVERE, this.getText("problemas.enviar.email") + user.getEmail() + ".");
                //message to the user
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, this.getText("problemas.enviar.email2"), null));

            }

        }
    }
    
    /**
     * @param recaida the recaida to set
     */
    public void setRecaida(String recaida) {
        this.recaida = recaida;
    }
    
    public boolean situacoesRisco(){
           return isRecaidaSituacao11()|| isRecaidaSituacao22()|| isRecaidaSituacao33();
    }
    
    public boolean comoEnfrentar(){
        return isRecaidaLidar11() || isRecaidaLidar22() || isRecaidaLidar33();
    }
    
    public boolean isRecaidaSituacao11(){
        return getAcompanhamento().getRecaidaSituacao1()!= null && !getAcompanhamento().getRecaidaSituacao1().trim().equals("");
    }
    
    public boolean isRecaidaSituacao22(){
        return acompanhamento.getRecaidaSituacao2()!= null && !acompanhamento.getRecaidaSituacao2().trim().equals("");
    }
    
    public boolean isRecaidaSituacao33(){
        return acompanhamento.getRecaidaSituacao3()!= null && !acompanhamento.getRecaidaSituacao3().trim().equals("");
    }

    public boolean isRecaidaLidar11(){
        return acompanhamento.getRecaidaLidar1() != null && !acompanhamento.getRecaidaLidar1().trim().equals("");
    }
    
    public boolean isRecaidaLidar22(){
        return this.getAcompanhamento().getRecaidaLidar2() != null && !acompanhamento.getRecaidaLidar2().trim().equals("");
    }
    
    public boolean isRecaidaLidar33(){
        return acompanhamento.getRecaidaLidar3() != null && !acompanhamento.getRecaidaLidar3().trim().equals("");
    }
            
    public Acompanhamento getAcompanhamento() {

        if (this.acompanhamento == null) {

            GregorianCalendar gc = (GregorianCalendar) GregorianCalendar.getInstance();
            gc.add(GregorianCalendar.HOUR, ParouDeFumarController.LIMITE_IGUALDADE_HORAS);

            //System.out.println("Dia: " + gc.get( GregorianCalendar.DAY_OF_MONTH ));
            //System.out.println("Hora: " + gc.get( GregorianCalendar.HOUR_OF_DAY ));
            Object object = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loggedUser");
            if (object != null) {
                try {
                    //System.out.println( "inject: " + this.loginController.getUser().toString() );
                    //List<ProntoParaParar> ppps = this.getDaoBase().list("usuario.id", ((User)object).getId(), this.getEntityManager());
                    List<Acompanhamento> as = this.getDaoBase().list("usuario", object, this.getEntityManager());

                    for (Acompanhamento a : as) {

                        //System.out.println("Verificando acompanhamento: " + a.getId());
                        GregorianCalendar calendar = new GregorianCalendar();
                        calendar.setTime(a.getDataInserido());
                        if (gc.after(calendar)) {
                            this.acompanhamento = a;
                            //System.out.println("Acompanhamento continua.");
                        }

                    }

                    if (this.acompanhamento == null) {

                        this.acompanhamento = new Acompanhamento();
                        this.acompanhamento.setUsuario((User) object);

                    }

                } catch (SQLException ex) {
                    Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, this.getText("usuario.acompanhado"));
                this.acompanhamento = new Acompanhamento();
            }

        }

        return this.acompanhamento;

    }

    public ByteArrayOutputStream gerarPdf2() {

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, os);

            document.open();

            document.addTitle(this.getText("meu.plano"));
            document.addAuthor("vivasemtabaco.com.br");

            URL url;
            url = FacesContext.getCurrentInstance().getExternalContext().getResource("/resources/default/images/viva-sem-tabaco-new.png");

            Image img;
            img = Image.getInstance(url);
            img.setAlignment(Element.ALIGN_CENTER);
            img.scaleToFit(75, 75);
            document.add(img);

            Color color = Color.getHSBColor(214, 81, 46);
            Font f1 = new Font(FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.BLUE);
            f1.setColor(22, 63, 117);
            Font f2 = new Font(FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLUE);
            f2.setColor(22, 63, 117);
            Font f3 = new Font(FontFamily.HELVETICA, 11);

            Paragraph paragraph = new Paragraph(this.getText("meu.plano"), f1);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            document.add( Chunk.NEWLINE );
            document.add( Chunk.NEWLINE );

            if(this.situacoesRisco()){
                paragraph = new Paragraph(this.getText("acompanhamento.plano.h2.1"), f2);
                document.add(paragraph);
                paragraph = new Paragraph(this.getAcompanhamento().getRecaidaSituacao1(), f3);
                document.add(paragraph);
                paragraph = new Paragraph(this.getAcompanhamento().getRecaidaSituacao2(), f3);
                document.add(paragraph);
                paragraph = new Paragraph(this.getAcompanhamento().getRecaidaSituacao3(), f3);
                document.add(paragraph);
                document.add( Chunk.NEWLINE );
            } else {
                paragraph.add(new Paragraph(" "));
            }
            //paragraph.add(new Paragraph(" "));

            if(this.comoEnfrentar()){
                paragraph = new Paragraph(this.getText("acompanhamento.plano.h2.1"), f2);
                document.add(paragraph);
                paragraph = new Paragraph(this.getAcompanhamento().getRecaidaLidar1(), f3);
                document.add(paragraph);
                paragraph = new Paragraph(this.getAcompanhamento().getRecaidaLidar2(), f3);
                document.add(paragraph);
                paragraph = new Paragraph(this.getAcompanhamento().getRecaidaLidar3(), f3);
                document.add(paragraph);
            } else{
                paragraph.add(new Paragraph(" "));
            }

            document.close();

            return os;
        } catch (DocumentException ex) {
            Logger.getLogger(ParouDeFumarController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        } catch (MalformedURLException ex) {
            Logger.getLogger(ParouDeFumarController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        } catch (IOException ex) {
            Logger.getLogger(ParouDeFumarController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }

    }
    
    public StreamedContent getLapsoRecaida() {

        InputStream is;
        try {
            is = new ByteArrayInputStream(gerarPdf2().toByteArray());
            return new DefaultStreamedContent(is, "application/pdf", "plano.pdf");

        } catch (Exception e) {
            Logger.getLogger(ParouDeFumarController.class
                    .getName()).log(Level.SEVERE, this.getText("erro.pdf"));
            Logger.getLogger(ParouDeFumarController.class.getName()).log(Level.SEVERE, e.getMessage(), e);

            return null;
        }

    }
    
    
    public ByteArrayOutputStream gerarPdfPadrao() {

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, os);

            document.open();

            document.addTitle(this.getText("recaida"));
            document.addAuthor("vivasemtabaco.com.br");

            URL url;
            url = FacesContext.getCurrentInstance().getExternalContext().getResource("/resources/default/images/viva-sem-tabaco-new.png");

            Image img;
            img = Image.getInstance(url);
            img.setAlignment(Element.ALIGN_CENTER);
            img.scaleToFit(75, 75);
            document.add(img);

            Color color = Color.getHSBColor(214, 81, 46);
            Font f1 = new Font(FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.BLUE);
            f1.setColor(22, 63, 117);
            Font f2 = new Font(FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLUE);
            f2.setColor(22, 63, 117);
            Font f3 = new Font(FontFamily.HELVETICA, 11);

            Paragraph paragraph = new Paragraph(this.getText("meu.plano"), f1);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            document.add( Chunk.NEWLINE );
            document.add( Chunk.NEWLINE );

            paragraph = new Paragraph(this.getText("acompanhamento.plano.padrao.h2.1"), f2);
            document.add(paragraph);
            paragraph = new Paragraph(this.getText("lembre.se2"), f3);
            document.add(paragraph);
            paragraph = new Paragraph(this.getText("lembre.se3"), f3);
            document.add(paragraph);
            document.add( Chunk.NEWLINE );

            paragraph = new Paragraph(this.getText("acompanhamento.plano.padrao.h2.2"), f2);
            document.add(paragraph);
            paragraph = new Paragraph(this.getText("recomendamos1"), f3);
            document.add(paragraph);
            paragraph = new Paragraph(this.getText("recomendamos2"), f3);
            document.add(paragraph);
            paragraph = new Paragraph(this.getText("recomendamos3"), f3);
            document.add(paragraph);

            document.close();

            return os;
        } catch (DocumentException ex) {
            Logger.getLogger(ParouDeFumarController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        } catch (MalformedURLException ex) {
            Logger.getLogger(ParouDeFumarController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        } catch (IOException ex) {
            Logger.getLogger(ParouDeFumarController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
            return null;
        }

    }
    
     public StreamedContent getRecaidaPadrao() {

        InputStream is;
        try {
            is = new ByteArrayInputStream(gerarPdfPadrao().toByteArray());
            return new DefaultStreamedContent(is, "application/pdf", "plano.pdf");

        } catch (Exception e) {
            Logger.getLogger(ParouDeFumarController.class
                    .getName()).log(Level.SEVERE, this.getText("erro.pdf"));
            Logger.getLogger(ParouDeFumarController.class.getName()).log(Level.SEVERE, e.getMessage(), e);

            return null;
        }

    }


}
