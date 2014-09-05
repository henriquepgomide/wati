/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wati.controller;

import static com.sun.corba.se.spi.presentation.rmi.StubAdapter.request;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.el.ELContext;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import wati.model.User;
import wati.persistence.GenericDAO;
import wati.utility.Encrypter;

/**
 *
 * @author hedersb
 */
@ManagedBean(name = "userController")
@SessionScoped
public class UserController extends BaseFormController<User> {

    private User user;

    private String password;

    private int dia;
    private int mes;
    private int ano;

    private boolean showErrorMessage;

    private Map<String, String> dias = new LinkedHashMap<String, String>();
    private Map<String, String> meses = new LinkedHashMap<String, String>();
    private Map<String, String> anos = new LinkedHashMap<String, String>();
    private String[] nomeMeses;

    @PersistenceContext
    private EntityManager entityManager = null;

    private GenericDAO dao = null;

    /**
     * Creates a new instance of UserController
     */
    public UserController() {

        super(User.class);

        this.showErrorMessage = false;
        
        mes = -1;

        for (int i = 1; i <= 31; i++) {
            dias.put(String.valueOf(i), String.valueOf(i));
        }


       /* for (int i = 1; i <= 12; i++) {
            //meses.put(this.nomeMeses[ i], String.valueOf(i+1));
            meses.put(this.getText("month." + String.valueOf(i)),
                    String.valueOf(i - 1));
        }*/

        GregorianCalendar gc = (GregorianCalendar) GregorianCalendar.getInstance();
        int lastYear = gc.get(GregorianCalendar.YEAR) - 1;
        for (int i = lastYear; i > lastYear - 100; i--) {
            anos.put(String.valueOf(i), String.valueOf(i));
        }

        try {
            dao = new GenericDAO(User.class);
        } catch (NamingException ex) {
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * @return the user
     */
    public User getUser() {
        if (user == null) {
            String id = this.getParameterMap().get("id");
            if (id == null || id.isEmpty()) {
                this.user = new User();
            } else {
                try {
                    List<User> list = this.getDaoBase().list("id", Long.parseLong(id), this.entityManager);
                    if (list.isEmpty()) {
                        this.user = new User();
                    } else {
                        this.user = list.get(0);
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
                    this.user = new User();
                }
            }

        }
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        if (this.password == null) {
            this.password = this.user == null || this.user.getPassword() == null ? "" : this.user.getPassword().toString();
        }
        return this.password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    public void sendEmailPassword() throws SQLException{
        
        try {
            List<User> userList = this.getDaoBase().list("email", this.user.getEmail(), this.getEntityManager());
            if(userList.isEmpty())
                System.out.println("Usuário nao cadastrado solicitando alteração de senha");
            else{
                String name_user = this.user.getName();
                String email_user = this.user.getEmail();
                
                     
        }
           
        
        /*
        if (userList.isEmpty() || !Encrypter.compare(this.password, userList.get(0).getPassword())) {
                //log message
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, this.getText("usuario.email") + this.getUser().getEmail() + this.getText("not.login"));
                //message to the user
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, this.getText("email.senha"), null));

            } 
        
        String email_usuario = request.getParameter("email")==null?"":request.getParameter("email"); 
    
        Object object = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("loggedUser");

        if (object == null) {

            Logger.getLogger(ProntoParaPararController.class.getName()).log(Level.SEVERE, this.getText("user.not.logged"));
            //message to the user
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, this.getText("deve.estar.logado"), null));

        
        String mensagem;  
                mensagem = "Olá" + "<br>" +  
                        "<br>" +  
                        "<P> Recebemos uma solicitação para informação dos dados de autenticação para este e-mail, caso não tenha feito esta solicitação, favor desconsiderar o mesmo.</P>" +  
                        "<BR>" +  
                        "<P>Caso tenha sido você, favor entrar no seguinte link: </P>" +  
                        "<UL>" +  
                        
                        "<P> Att, </p>" +  
                        "<BR>" +  
                        "Equipe Viva sem Tabaco" +  
                        "<BR> \n";  
                  
                String mailServer = "smtp.gmail.com";  
                String assunto = "Lembraça de Senha"; */


        //Read more: http://javafree.uol.com.br/artigo/864641/Enviar-Email-tipo-Esqueci-Senha-3-Camadas.html#ixzz3CRj4z073

        }
    }
    }
    
    public void alterPassword(){
        this.showErrorMessage = true;
        try{
            if (!(dao.list("email", user.getEmail(), entityManager).isEmpty())){
                
                this.user.setPassword(Encrypter.encrypt(this.password));
                
            }
            else{
                String message = "Usuário nao cadastrado";
            }
        }
        catch (InvalidKeyException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, this.getText("problemas.gravar.usuario"), null));
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, this.getText("problemas.gravar.usuario"), null));
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, this.getText("problemas.gravar.usuario"), null));
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, this.getText("problemas.gravar.usuario"), null));
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, this.getText("problemas.gravar.usuario"), null));
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, this.getText("problemas.gravar.usuario"), null));
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);

        }
            
        
    }

    public void save(ActionEvent actionEvent) {

        this.showErrorMessage = true;
        this.user.setBirth(new GregorianCalendar(ano, mes, dia).getTime());

        try {
            if (!(dao.list("email", user.getEmail(), entityManager).isEmpty())) {
                String message = this.getText("email.cadastrado");
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, message, null));
            } else {

                if (user.getId() == 0) {

                    //incluir criptografia da senha
                    this.user.setPassword(Encrypter.encrypt(this.password));

                } else {

                    if (!Encrypter.compare(this.password, this.user.getPassword())) {
                        //incluir criptografia da senha
                        this.user.setPassword(Encrypter.encrypt(this.password));
                    }

                }
                
                Locale locale =  (Locale) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("locale");
                if(locale == null ){
                    locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
                }
                this.user.setPreferedLanguage(locale.getLanguage());

                super.save(actionEvent, entityManager);
                //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage( FacesMessage.SEVERITY_INFO, "Usuário criado com sucesso.", null ));
                ELContext elContext = FacesContext.getCurrentInstance().getELContext();
                LoginController login = (LoginController) FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null, "loginController");
                login.setUser(this.user);
                login.setPassword(this.password);
                login.loginDialog();
                try { 
                    FacesContext.getCurrentInstance().getExternalContext().redirect("escolha-uma-etapa.xhtml");
                    //FacesContext.getCurrentInstance().getExternalContext().redirect("index.xhtml");
                } catch (IOException ex) {
                    Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
                }
                this.clear();
            }

        } catch (InvalidKeyException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, this.getText("problemas.gravar.usuario"), null));
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalBlockSizeException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, this.getText("problemas.gravar.usuario"), null));
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadPaddingException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, this.getText("problemas.gravar.usuario"), null));
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, this.getText("problemas.gravar.usuario"), null));
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchPaddingException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, this.getText("problemas.gravar.usuario"), null));
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, this.getText("problemas.gravar.usuario"), null));
            Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

    /**
     * @return the dia
     */
    public int getDia() {
        return dia;
    }

    /**
     * @param dia the dia to set
     */
    public void setDia(int dia) {
        this.dia = dia;
    }

    /**
     * @return the mes
     */
    public int getMes() {
        return mes;
    }

    /**
     * @param mes the mes to set
     */
    public void setMes(int mes) {
        this.mes = mes;
    }

    /**
     * @return the ano
     */
    public int getAno() {
        return ano;
    }

    /**
     * @param ano the ano to set
     */
    public void setAno(int ano) {
        this.ano = ano;
    }

    /**
     * @return the dias
     */
    public Map<String, String> getDias() {
        return dias;
    }

    /**
     * @param dias the dias to set
     */
    public void setDias(Map<String, String> dias) {
        this.dias = dias;
    }

    /**
     * @return the meses
     */
    public Map<String, String> getMeses() {
        meses.clear();
        for (int i = 1; i <= 12; i++) {
            meses.put(this.getText("month." + String.valueOf(i)),
                    String.valueOf(i - 1));
        }
        return meses;
    }

    /**
     * @param meses the meses to set
     */
    public void setMeses(Map<String, String> meses) {
        this.meses = meses;
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

    /**
     * @return the showErrorMessage
     */
    public boolean isShowErrorMessage() {
        return showErrorMessage;
    }

    /**
     * @param showErrorMessage the showErrorMessage to set
     */
    public void setShowErrorMessage(boolean showErrorMessage) {
        this.showErrorMessage = showErrorMessage;
    }

    private void clear() {
        this.ano = 0;
        this.dia = 0;
        this.mes = -1;
        this.password = "";
        this.user = new User();
    }
}
