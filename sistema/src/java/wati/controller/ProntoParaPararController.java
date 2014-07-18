/*
 * To change this template, choose Tools | Templates
 * and open the template in 
 the editor.
 */
package wati.controller;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import wati.model.ProntoParaParar;
import wati.model.User;
import wati.persistence.GenericDAO;
import wati.utility.EMailSSL;

/**
 *
 * @author hedersb
 */
@ManagedBean(name = "prontoParaPararController")
@SessionScoped
public class ProntoParaPararController extends BaseController<ProntoParaParar> {

    private ProntoParaParar prontoParaParar = null;
    private String[] vencendoAFissuraMarcados = null;
    private static final int VENCENDO_FISSURA_BEBER_AGUA = 0;
    private static final int VENCENDO_FISSURA_COMER = 1;
    private static final int VENCENDO_FISSURA_RELAXAMENTO = 2;
    private static final int VENCENDO_FISSURA_LER_RAZOES = 3;
    private Map<String, String> anos = new LinkedHashMap<String, String>();
    private GregorianCalendar gregorianCalendar = null;
    private StreamedContent planoPersonalizado;

    public ProntoParaPararController() {
        //super(ProntoParaParar.class);
        try {
            this.daoBase = new GenericDAO<ProntoParaParar>(ProntoParaParar.class);
        } catch (NamingException ex) {
            String message = "Ocorreu um erro inesperado.";
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, message, null));
            Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }

        GregorianCalendar gc = (GregorianCalendar) GregorianCalendar.getInstance();
        int firstYear = gc.get(GregorianCalendar.YEAR);
        for (int i = firstYear; i < firstYear + 5; i++) {
            anos.put(String.valueOf(i), String.valueOf(i));
        }

    }

    public String vencendoAFissura() {
        this.prontoParaParar.limparVencendoFissura();

        for (String string : vencendoAFissuraMarcados) {

            switch (Integer.valueOf(string)) {
                case VENCENDO_FISSURA_BEBER_AGUA:
                    this.prontoParaParar.setEnfrentarFissuraBeberAgua(true);
                    break;
                case VENCENDO_FISSURA_COMER:
                    this.prontoParaParar.setEnfrentarFissuraComer(true);
                    break;
                case VENCENDO_FISSURA_LER_RAZOES:
                    this.prontoParaParar.setEnfrentarFissuraLerRazoes(true);
                    break;
                case VENCENDO_FISSURA_RELAXAMENTO:
                    this.prontoParaParar.setEnfrentarFissuraRelaxamento(true);
                    break;
            }

        }
        try {
            this.getDaoBase().insertOrUpdate(prontoParaParar, this.getEntityManager());
            return "pronto-para-parar-de-fumar-medicamentos.xhtml";
        } catch (SQLException ex) {
            Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String fumarMetodosDeParar() {

        return "pronto-para-parar-de-fumar-metodos-de-parar.xhtml";

    }

    public String dataParaParar() {

//		if ( this.ano.equals("Atual") ) {
//			this.ano = String.valueOf( new GregorianCalendar().get( GregorianCalendar.YEAR ) );
//		}
//		
//		if ( this.mes.equals("Atual") ) {
//			this.mes = String.valueOf( new GregorianCalendar().get( GregorianCalendar.MONTH ) );
//		}
//		
//		if ( this.dia.equals("Atual") ) {
//			this.dia = String.valueOf( new GregorianCalendar().get( GregorianCalendar.DAY_OF_MONTH ) );
//		}
        this.prontoParaParar.setDataParar(this.gregorianCalendar.getTime());

        try {
            this.getDaoBase().insertOrUpdate(prontoParaParar, this.getEntityManager());
            return "pronto-para-parar-de-fumar-como-evitar-recaidas.xhtml";
        } catch (SQLException ex) {
            Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Problemas ao gravar a data de parar de fumar.", null));
        }
        return null;
    }

    public String recaidaTentouParar() {

        try {
            this.getDaoBase().insertOrUpdate(this.prontoParaParar, this.getEntityManager());
            if (this.prontoParaParar.isTentouParar()) {
                return "pronto-para-parar-de-fumar-como-evitar-recaidas-sim.xhtml";
            } else {
                return "pronto-para-parar-de-fumar-como-evitar-recaidas-completo.xhtml";
            }
        } catch (SQLException ex) {
            Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Problemas ao gravar a data de parar de fumar.", null));
        }
        return null;
    }

    public String recaidasAjudar() {

        try {
            this.getDaoBase().insertOrUpdate(this.prontoParaParar, this.getEntityManager());
            return "pronto-para-parar-de-fumar-como-evitar-recaidas-completo.xhtml";
            //return "pronto-para-parar-de-fumar-ganho-de-peso.xhtml";
        } catch (SQLException ex) {
            Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Problemas ao gravar a data de parar de fumar.", null));
        }
        return null;
    }

    public String recaidasEvitar() {

        try {
            this.getDaoBase().insertOrUpdate(this.prontoParaParar, this.getEntityManager());
            return "pronto-para-parar-de-fumar-ganho-de-peso.xhtml";
        } catch (SQLException ex) {
            Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Problemas ao gravar a data de parar de fumar.", null));
        }
        return null;
    }

    /**
     * @return the vencendoAFissuraMarcados
     */
    public String[] getVencendoAFissuraMarcados() {

        if (this.vencendoAFissuraMarcados == null) {

            ProntoParaParar ppp = this.getProntoParaParar();

            int count = 0;
            if (ppp.isEnfrentarFissuraBeberAgua()) {
                count++;
            }
            if (ppp.isEnfrentarFissuraComer()) {
                count++;
            }
            if (ppp.isEnfrentarFissuraLerRazoes()) {
                count++;
            }
            if (ppp.isEnfrentarFissuraRelaxamento()) {
                count++;
            }
            this.vencendoAFissuraMarcados = new String[count];
            count = 0;
            if (ppp.isEnfrentarFissuraBeberAgua()) {
                this.vencendoAFissuraMarcados[ count] = String.valueOf(ProntoParaPararController.VENCENDO_FISSURA_BEBER_AGUA);
                count++;
            }
            if (ppp.isEnfrentarFissuraComer()) {
                this.vencendoAFissuraMarcados[ count] = String.valueOf(ProntoParaPararController.VENCENDO_FISSURA_COMER);
                count++;
            }
            if (ppp.isEnfrentarFissuraLerRazoes()) {
                this.vencendoAFissuraMarcados[ count] = String.valueOf(ProntoParaPararController.VENCENDO_FISSURA_LER_RAZOES);
                count++;
            }
            if (ppp.isEnfrentarFissuraRelaxamento()) {
                this.vencendoAFissuraMarcados[ count] = String.valueOf(ProntoParaPararController.VENCENDO_FISSURA_RELAXAMENTO);
                count++;
            }
        }

        return vencendoAFissuraMarcados;
    }

    /**
     * @param vencendoAFissuraMarcados the vencendoAFissuraMarcados to set
     */
    public void setVencendoAFissuraMarcados(String[] vencendoAFissuraMarcados) {
        this.vencendoAFissuraMarcados = vencendoAFissuraMarcados;
    }

    public void save(ActionEvent actionEvent) {
    }

    /**
     * @return the dia
     */
    public int getDia() {
        if (this.gregorianCalendar == null) {
            getProntoParaParar();
        }
        return this.gregorianCalendar.get(GregorianCalendar.DAY_OF_MONTH);
    }

    /**
     * @param dia the dia to set
     */
    public void setDia(int dia) {
        if (this.gregorianCalendar == null) {
            getProntoParaParar();
        }
        this.gregorianCalendar.set(GregorianCalendar.DAY_OF_MONTH, dia);
    }

    /**
     * @return the mes
     */
    public int getMes() {
        if (this.gregorianCalendar == null) {
            getProntoParaParar();
        }
        return this.gregorianCalendar.get(GregorianCalendar.MONTH);
    }

    /**
     * @param mes the mes to set
     */
    public void setMes(int mes) {
        if (this.gregorianCalendar == null) {
            getProntoParaParar();
        }
        this.gregorianCalendar.set(GregorianCalendar.MONTH, mes);
    }

    /**
     * @return the ano
     */
    public int getAno() {
        if (this.gregorianCalendar == null) {
            getProntoParaParar();
        }
        return this.gregorianCalendar.get(GregorianCalendar.YEAR);
    }

    /**
     * @param ano the ano to set
     */
    public void setAno(int ano) {
        if (this.gregorianCalendar == null) {
            getProntoParaParar();
        }
        this.gregorianCalendar.set(GregorianCalendar.YEAR, ano);
    }

    /**
     * @return the prontoParaParar
     */
    public ProntoParaParar getProntoParaParar() {
        if (this.prontoParaParar == null) {
            this.gregorianCalendar = (GregorianCalendar) GregorianCalendar.getInstance();
            Object object = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loggedUser");
            if (object != null) {
                try {
                    //System.out.println( "inject: " + this.loginController.getUser().toString() );
                    //List<ProntoParaParar> ppps = this.getDaoBase().list("usuario.id", ((User)object).getId(), this.getEntityManager());
                    List<ProntoParaParar> ppps = this.getDaoBase().list("usuario", object, this.getEntityManager());
                    if (ppps.size() > 0) {
                        this.prontoParaParar = ppps.get(0);
                        this.gregorianCalendar.setTime(this.prontoParaParar.getDataParar());
                    } else {
                        this.prontoParaParar = new ProntoParaParar();
                        this.prontoParaParar.setDataParar(this.gregorianCalendar.getTime());
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, null, ex);
                }
                this.prontoParaParar.setUsuario((User) object);
            } else {
                Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, "Usuário não logado preenchendo ficha.");
            }
        }

        return prontoParaParar;
    }

    /**
     * @param prontoParaParar the prontoParaParar to set
     */
    public void setProntoParaParar(ProntoParaParar prontoParaParar) {
        this.prontoParaParar = prontoParaParar;
    }

    /**
     * @return the anos
     */
    public Map<String, String> getAnos() {
        return anos;
    }

    /**
     * @param anos the anos to set
     */
    public void setAnos(Map<String, String> anos) {
        this.anos = anos;
    }

    public List<ProntoParaParar> getProntoParaPararList() {
        ArrayList<ProntoParaParar> arrayList = new ArrayList<ProntoParaParar>();
        arrayList.add(prontoParaParar);
        return arrayList;
    }

    public boolean isEvitarRecaidaFara11() {
        return prontoParaParar.getEvitarRecaidaFara1() != null && !prontoParaParar.getEvitarRecaidaFara1().trim().equals("");
    }

    public boolean isEvitarRecaidaFara22() {
        return prontoParaParar.getEvitarRecaidaFara2() != null && !prontoParaParar.getEvitarRecaidaFara2().trim().equals("");
    }

    public boolean isEvitarRecaidaFara33() {
        return prontoParaParar.getEvitarRecaidaFara3() != null && !prontoParaParar.getEvitarRecaidaFara3().trim().equals("");
    }

    public boolean isEvitarRecaida11() {
        return prontoParaParar.getEvitarRecaida1() != null && !prontoParaParar.getEvitarRecaida1().trim().equals("");
    }

    public boolean isEvitarRecaida22() {
        return prontoParaParar.getEvitarRecaida2() != null && !prontoParaParar.getEvitarRecaida2().trim().equals("");
    }

    public boolean isEvitarRecaida33() {
        return prontoParaParar.getEvitarRecaida3() != null && !prontoParaParar.getEvitarRecaida3().trim().equals("");
    }

    public boolean tecnicaFissura() {
        return false;
    }

    public boolean fissuras() {
        if (prontoParaParar.isEnfrentarFissuraBeberAgua() == false && prontoParaParar.isEnfrentarFissuraComer() == false && prontoParaParar.isEnfrentarFissuraLerRazoes() == false && prontoParaParar.isEnfrentarFissuraRelaxamento() == false) {
            return false;
        } else {
            return true;
        }
    }

    public boolean estrategias() {
        return isEvitarRecaidaFara11() || isEvitarRecaidaFara22() || isEvitarRecaidaFara33();
    }

    public boolean deucerto() {
        return isEvitarRecaida11() || isEvitarRecaida22() || isEvitarRecaida33();
    }

    public void enviarEmail() {

        Object object = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loggedUser");

        if (object == null) {

            Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, "Usuário não logado no sistema requerendo plano.");
            //message to the user
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Você deve estar logado no sistema para solitar o envio do e-mail.", null));

        } else {

            User user = (User) object;

            try {
                String from = "watiufjf@gmail.com";
                String to = user.getEmail();
                String subject = "Plano Personalizado -- Wati";
                
                String html =  this.defaultEmail("Plano Personalizado", this.planoToHTML(user));

                EMailSSL eMailSSL = new EMailSSL();
                System.out.println(html);
                eMailSSL.send(from, to, subject, this.planoToText(user), html, gerarPdf());

                Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.INFO, "Plano personalizado enviado para o e-mail " + user.getEmail() + ".");
                //message to the user
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "E-mail enviado com sucesso.", null));

            } catch (Exception ex) {

                Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, "Problemas ao enviar e-mail para " + user.getEmail() + ".");
                //message to the user
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Problemas ao enviar e-mail. Por favor, tente novamente mais tarde.", null));

            }

        }

    }

    public String planoToHTML(User user) {
        String html = "   Prezado " + user.getName() + ",<br><br>"
                + "Segue abaixo seu plano personalizado:<br>"
                + "<br>Data de parada:<br>"
                + this.prontoParaParar.getDataPararStr() +"<br>"
                + "<br>Técnicas para fissura:<br>"
                + this.prontoParaParar.getFissuraStr() +"<br>"
                + "<br>Estratégias para resistir ao cigarro:<br>";
        if (this.prontoParaParar.getEvitarRecaidaFara1() != null && !this.prontoParaParar.getEvitarRecaidaFara1().isEmpty()) {
            html += this.prontoParaParar.getEvitarRecaidaFara1()+"<br>";
        }
        if (this.prontoParaParar.getEvitarRecaidaFara2() != null && !this.prontoParaParar.getEvitarRecaidaFara2().isEmpty()) {
            html += this.prontoParaParar.getEvitarRecaidaFara2()+"<br>";
        }
        if (this.prontoParaParar.getEvitarRecaidaFara3() != null && !this.prontoParaParar.getEvitarRecaidaFara3().isEmpty()) {
            html += this.prontoParaParar.getEvitarRecaidaFara3()+"<br>";
        }
                html += "<br>O que deu certo da última vez que parei:<br>";
        if (this.prontoParaParar.getEvitarRecaida1() != null && !this.prontoParaParar.getEvitarRecaida1().isEmpty()) {
            html += this.prontoParaParar.getEvitarRecaida1()+"<br>";
        }
        if (this.prontoParaParar.getEvitarRecaida2() != null && !this.prontoParaParar.getEvitarRecaida2().isEmpty()) {
            html += this.prontoParaParar.getEvitarRecaida2()+"<br>";
        }
        if (this.prontoParaParar.getEvitarRecaida3() != null && !this.prontoParaParar.getEvitarRecaida3().isEmpty()) {
            html += this.prontoParaParar.getEvitarRecaida3()+"<br>";
        }
            html += "<br>Atenciosamente.";
        return html;
    }

    public String planoToText(User user) {
        String text = "Prezado " + user.getName() + ",\n\n"
                + "Segue abaixo seu plano personalizado:\n\n"
                + "Data de parada:\n"
                + this.prontoParaParar.getDataPararStr()
                + "\nTécnicas para fissura:\n"
                + this.prontoParaParar.getFissuraStr()
                + "\nEstratégias para resistir ao cigarro:\n";
        if (this.prontoParaParar.getEvitarRecaidaFara1() != null && !this.prontoParaParar.getEvitarRecaidaFara1().isEmpty()) {
            text += this.prontoParaParar.getEvitarRecaidaFara1() + "\n";
        }
        if (this.prontoParaParar.getEvitarRecaidaFara2() != null && !this.prontoParaParar.getEvitarRecaidaFara2().isEmpty()) {
            text += this.prontoParaParar.getEvitarRecaidaFara2() + "\n";
        }
        if (this.prontoParaParar.getEvitarRecaidaFara3() != null && !this.prontoParaParar.getEvitarRecaidaFara3().isEmpty()) {
            text += this.prontoParaParar.getEvitarRecaidaFara3() + "\n";
        }
        text += "O que deu certo da última vez que parei:\n";
        if (this.prontoParaParar.getEvitarRecaida1() != null && !this.prontoParaParar.getEvitarRecaida1().isEmpty()) {
            text += this.prontoParaParar.getEvitarRecaida1() + "\n";
        }
        if (this.prontoParaParar.getEvitarRecaida2() != null && !this.prontoParaParar.getEvitarRecaida2().isEmpty()) {
            text += this.prontoParaParar.getEvitarRecaida2() + "\n";
        }
        if (this.prontoParaParar.getEvitarRecaida3() != null && !this.prontoParaParar.getEvitarRecaida3().isEmpty()) {
            text += this.prontoParaParar.getEvitarRecaida3() + "\n";
        }
        text += "\n\nAtenciosamente.\n";
        return text;
    }

    public ByteArrayOutputStream gerarPdf() {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();

            Document document = new Document();
            PdfWriter.getInstance(document, os);
            document.open();

            document.addTitle("Plano Persolanizado");
            document.addAuthor("vivasemtabaco.com.br");

            URL url = FacesContext.getCurrentInstance().getExternalContext().getResource("/resources/default/images/viva-sem-tabaco-new.png");
            Image img = Image.getInstance(url);
            img.setAlignment(Element.ALIGN_CENTER);
            img.scaleToFit(75, 75);
            document.add(img);

            Color color = Color.getHSBColor(214, 81, 46);
            Font f1 = new Font(FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.BLUE);
            f1.setColor(22, 63, 117);
            Font f2 = new Font(FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLUE);
            f2.setColor(22, 63, 117);
            Font f3 = new Font(FontFamily.HELVETICA, 12);

            Paragraph paragraph = new Paragraph("Meu Plano", f1);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            document.add(paragraph);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);

            paragraph = new Paragraph("Dicas", f2);
            document.add(paragraph);
            paragraph = new Paragraph("1. Ao parar de fumar você melhora sua qualidade de vida e vive mais tempo.", f3);
            document.add(paragraph);
            paragraph = new Paragraph("2. A vontade de fumar é muito comum nas pessoas que estão parando de fumar. Para a maioria dos fumantes, ela diminui nas primeiras semanas após você parar.", f3);
            document.add(paragraph);
            paragraph = new Paragraph("3. Manter a abstinência é fundamental, logo evite fumar após a data escolhida.", f3);
            document.add(paragraph);
            paragraph = new Paragraph("4. Medicamentos dobram suas chances de conseguir parar. Não deixe de procurar um profissional de saúde.", f3);
            document.add(paragraph);
            paragraph = new Paragraph("5. Evite situações de risco na primeira semana como: ", f3);
            document.add(paragraph);
            paragraph = new Paragraph("     - Usar de bebidas alcóolicas.", f3);
            document.add(paragraph);
            paragraph = new Paragraph("     - Avise também aos seus amigos e familiares que fumam que você está tentando parar.", f3);
            document.add(paragraph);
            paragraph = new Paragraph("6. É normal fumantes ganharem peso. Pode ser uma boa hora para começar a fazer exercícios físicos e comer mais frutas e legumes. ", f3);
            document.add(paragraph);
            document.add(Chunk.NEWLINE);

            paragraph = new Paragraph("Data de parada", f2);
            document.add(paragraph);
            paragraph = new Paragraph(this.prontoParaParar.getDataPararStr(), f3);
            document.add(paragraph);
            document.add(Chunk.NEWLINE);

            if (this.fissuras()) {
                paragraph = new Paragraph("Técnicas para fissura", f2);
                document.add(paragraph);
                paragraph = new Paragraph(this.prontoParaParar.getFissuraStr(), f3);
                document.add(paragraph);
                document.add(Chunk.NEWLINE);
            } else {
                paragraph.add(new Paragraph(" "));
            }

            if (this.estrategias()) {
                paragraph = new Paragraph("Estratégias para resistir ao cigarro", f2);
                document.add(paragraph);
                paragraph = new Paragraph(this.prontoParaParar.getEvitarRecaidaFara1(), f3);
                document.add(paragraph);
                paragraph = new Paragraph(this.prontoParaParar.getEvitarRecaidaFara2(), f3);
                document.add(paragraph);
                paragraph = new Paragraph(this.prontoParaParar.getEvitarRecaidaFara3(), f3);
                document.add(paragraph);
                document.add(Chunk.NEWLINE);
            } else {
                paragraph.add(new Paragraph(" "));
            }

            if (this.deucerto()) {
                paragraph = new Paragraph("O que deu certo da última vez que parei", f2);
                document.add(paragraph);
                paragraph = new Paragraph(this.prontoParaParar.getEvitarRecaida1(), f3);
                document.add(paragraph);
                paragraph = new Paragraph(this.prontoParaParar.getEvitarRecaida2(), f3);
                document.add(paragraph);
                paragraph = new Paragraph(this.prontoParaParar.getEvitarRecaida3(), f3);
                document.add(paragraph);
            } else {
                paragraph.add(new Paragraph(" "));
            }

            document.close();

            return os;
        } catch (Exception e) {
            return null;
        }

    }

    public StreamedContent getPlanoPersonalizado() {

        InputStream is;
        try {
            is = new ByteArrayInputStream(gerarPdf().toByteArray());
            return new DefaultStreamedContent(is, "application/pdf", "plano.pdf");
        } catch (Exception e) {
            Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, "Erro ao gerar o pdf");
            return null;
        }

    }

}
