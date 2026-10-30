package dashboard;

import affichage.DashboardGraph;
import affichage.DashboardGraphType;
import affichage.PageRecherche;
import affichage.TableauRecherche;
import bean.ClassMAPTable;

import javax.servlet.http.HttpServletRequest;

public class KpiCa  extends DashboardGraph {
    public KpiCa(ClassMAPTable base, HttpServletRequest req, String[] colValeur, String[] colAffiche, String[] legendes, String dateColonne, DashboardGraphType type) throws Exception {
        super(base, req, colValeur, colAffiche, legendes, dateColonne, type);
    }

    public KpiCa(PageRecherche pageRecherche, String[] colValeur, String[] colAffiche, String[] legendes, DashboardGraphType type) {
        super(pageRecherche, colValeur, colAffiche, legendes, type);
    }
    @Override
    public String makeCardHtml() {
        TableauRecherche recap = this.getPageRecherche().getTableauRecap();
        String value = recap.getDataDirecte()[0][2];
        return "<div class=\"col-md-12\" style=\"margin-bottom: 20px;\">\n" +
                "<a href=\"#\" class=\"kpi-card-modern kpi-blue-theme\">\n" +
                "<h3 class=\"kpi-title\">"+getTitre()+"</h3>\n" +
                "<div class=\"kpi-main-val\">"+value+" Ar</div> \n" +
                "</a>\n" +
                "</div>";
    }
}
